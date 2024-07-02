package com.anynote.ai.nio.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.anynote.ai.api.model.bo.WhisperTaskCreatedMQParam;
import com.anynote.ai.api.model.po.WhisperTask;
import com.anynote.ai.api.model.vo.WhisperTaskStatusVO;
import com.anynote.ai.nio.constants.WhisperConstants;
import com.anynote.ai.nio.datascope.annotation.RequiresWhisperTaskPermissions;
import com.anynote.ai.nio.model.bo.WhisperTaskQueryParam;
import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.api.model.vo.WhisperSubmitVO;
import com.anynote.ai.nio.model.vo.WhisperVO;
import com.anynote.ai.nio.service.WhisperService;
import com.anynote.ai.nio.service.WhisperTaskService;
import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.WhisperTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.model.bo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.google.gson.Gson;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class WhisperServiceImpl implements WhisperService {


    @Resource
    private ReactiveRedisTemplate<String, JSONObject> reactiveRedisTemplate;

    @Resource
    private ConfigService configService;

    @Resource
    private WebClient webClient;

    @Resource
    private Gson gson;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RocketMQProperties rocketMQProperties;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private WhisperTaskService whisperTaskService;



    @Override
    public Flux<ServerSentEvent<String>> whisper(WhisperDTO whisperDTO) {
//        Flux<ServerSentEvent<String>> heartbeatFlux = Flux.interval(Duration.ofSeconds(10))
//                .map(tick -> {
//                    log.info("heartbeat");
//                    return ServerSentEvent.<String>builder()
//                            .id(new Date().toString())
//                            .data("heartbeat")
//                            .event("heartbeat")
//                            .build();
//                });

        String aiServiceAddress = configService.getAIServerAddress();
//        return webClient.post()
//                .uri(aiServiceAddress + "/api/whisper/submit")
//                .body(Mono.just(whisperDTO), WhisperDTO.class)
//                .retrieve()
//                .bodyToMono(WhisperSubmitVO.class)
//                .flux()
//                .flatMap(whisperSubmitVO ->
//                    Flux.merge(heartbeatFlux, reactiveRedisTemplate
//                        .listenToChannel(RedisChannel.WHISPER_TASK_CHANNEL + whisperSubmitVO.getTaskId())
//                        .map(value -> {
//                            log.info(value.toString());
//                            return ServerSentEvent.<String>builder()
//                                    .id(String.valueOf(System.currentTimeMillis()))
//                                    .data(gson.toJson(value.getMessage()))
//                                    .event("message")
//                                    .build();
//                        }))
//                );
//        return Flux.merge(heartbeatFlux);

        return webClient.post()
                .uri(aiServiceAddress + "/api/whisper")
                .body(Mono.just(whisperDTO), WhisperDTO.class)
                .retrieve()
                .bodyToFlux(WhisperVO.class)
                .flatMap(whisperVO -> {
                    log.info(gson.toJson(whisperVO));
                    return Flux.just(ServerSentEvent
                            .<String>builder()
                            .id(String.valueOf(System.currentTimeMillis()))
                            .event("message")
                            .data(gson.toJson(whisperVO)).build());
                });
    }

    @Override
    public Mono<WhisperSubmitVO> submitWhisper(WhisperDTO whisperDTO, String accessToken) {
        String aiServiceAddress = configService.getAIServerAddress();
        LoginUser loginUser = tokenUtil.getLoginUser(accessToken);
        ParameterizedTypeReference<ResData<WhisperSubmitVO>> resType = new ParameterizedTypeReference<ResData<WhisperSubmitVO>>(){};
        Date createTime = new Date();
        return webClient.post()
                .uri(aiServiceAddress + WhisperConstants.WHISPER_TASK_SUBMIT_URL)
                .body(Mono.just(whisperDTO), WhisperDTO.class)
                .retrieve()
                .bodyToMono(resType)
                .flatMap(resData -> {
                    log.info(gson.toJson(resData));
                    WhisperSubmitVO whisperSubmitVO = resData.getData();
                    Date updateTime = new Date();
                    WhisperTask whisperTask = WhisperTask
                            .builder()
                            .taskId(whisperSubmitVO.getTaskId())
                            .taskStatus(WhisperTaskStatusVO.Status.STARTING.getValue())
                            .createTime(createTime)
                            .updateTime(updateTime)
                            .createBy(loginUser.getUserId())
                            .updateBy(loginUser.getUserId()).build();
                    whisperTaskService.getBaseMapper().insert(whisperTask);
                    String destination = rocketMQProperties.getNoteTopic() + ":" + WhisperTagsEnum.WHISPER_TASK_SUBMITTED.name();
                    rocketMQTemplate.asyncSend(destination, gson.toJson(WhisperTaskCreatedMQParam.builder()
                                            .whisperSubmitVO(whisperSubmitVO)
                                    .whisperTaskId(whisperTask.getId()).userId(loginUser.getUserId()).build()),
                            RocketmqSendCallbackBuilder.commonCallback());
                    return Mono.just(whisperSubmitVO);
                });
    }


    @RequiresWhisperTaskPermissions
    @Override
    public Flux<ServerSentEvent<WhisperTaskStatusVO>> whisperTaskStatus(WhisperTaskQueryParam queryParam) {

//        AtomicBoolean isListened = new AtomicBoolean(false);
        ReentrantLock listenLock = new ReentrantLock();
        Condition condition = listenLock.newCondition();
        AtomicBoolean isListened = new AtomicBoolean(false);
        Flux<ServerSentEvent<WhisperTaskStatusVO>> heartbeatFlux = Flux.interval(Duration.ofSeconds(10))
                .map(tick -> {
                    log.info("heartbeat");
                    return ServerSentEvent.<WhisperTaskStatusVO>builder()
                            .id(new Date().toString())
                            .event("heartbeat")
                            .build();
                });
        Flux<ServerSentEvent<WhisperTaskStatusVO>> statusFlux = Mono.fromCallable(() -> {
            listenLock.lock();
            try {
                while (!isListened.get()) {
                    condition.await();
                }
                log.info("query mysql");
                return whisperTaskService
                        .getBaseMapper().selectById(queryParam.getWhisperTaskId());
            } finally {
                listenLock.unlock();
            }
        }).flux()
                .flatMap(whisperTask -> {
                    log.info(gson.toJson(whisperTask));
                    return Flux.just(ServerSentEvent
                            .builder(WhisperTaskStatusVO.builder()
                                    .status(WhisperTaskStatusVO.Status.values()[whisperTask.getTaskStatus()].name())
                                    .taskId(whisperTask.getTaskId())
                                    .result(WhisperTaskStatusVO.WhisperTaskResult.builder()
                                            .srt(whisperTask.getSrtUrl())
                                            .txt(whisperTask.getTxtUrl()).build())
                                    .build())
                            .event("message")
                            .id(String.valueOf(System.currentTimeMillis())).build());
                })
                .subscribeOn(Schedulers.boundedElastic());

        String chanel = RedisChannel.WHISPER_TASK_STATUS_CHANNEL + queryParam.getWhisperTaskId();
        Flux<ServerSentEvent<WhisperTaskStatusVO>> redisSubFlux = reactiveRedisTemplate.listenToChannel(chanel)
                .map(message -> {
                    log.info(String.valueOf(message));
                    return ServerSentEvent
                            .builder(message.getMessage().toJavaObject(WhisperTaskStatusVO.class))
                            .build();
                }).doOnSubscribe(subscription -> {
                    listenLock.lock();
                    try {
                        isListened.set(true);
                        condition.signal();
                        log.info("chanel {} is listening", chanel);
                    } finally {
                        listenLock.unlock();
                    }
                });
        return Flux.merge(heartbeatFlux, redisSubFlux, statusFlux).takeUntil(sse -> StringUtils.isNotNull(sse.data()) &&
                WhisperTaskStatusVO.Status.FINISHED.name().equals(sse.data().getStatus()));
    }
}
