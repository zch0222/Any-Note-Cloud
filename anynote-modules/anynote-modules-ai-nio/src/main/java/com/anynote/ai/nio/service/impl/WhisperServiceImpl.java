package com.anynote.ai.nio.service.impl;

import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.nio.model.vo.WhisperSubmitVO;
import com.anynote.ai.nio.service.WhisperService;
import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.redis.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.google.gson.Gson;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Service
public class WhisperServiceImpl implements WhisperService {


    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Resource
    private ConfigService configService;

    @Resource
    private WebClient webClient;

    @Resource
    private Gson gson;



    @Override
    public Flux<ServerSentEvent<String>> whisper(WhisperDTO whisperDTO) {
        Flux<ServerSentEvent<String>> heartbeatFlux = Flux.interval(Duration.ofSeconds(10))
                .map(tick -> {
                    log.info("heartbeat");
                    return ServerSentEvent.<String>builder()
                            .id(new Date().toString())
                            .data("heartbeat")
                            .event("heartbeat")
                            .build();
                });

        String aiServiceAddress = configService.getAIServerAddress();
        return webClient.post()
                .uri(aiServiceAddress)
                .body(Mono.just(whisperDTO), WhisperDTO.class)
                .retrieve()
                .bodyToMono(WhisperSubmitVO.class)
                .flux()
                .flatMap(whisperSubmitVO ->
                    Flux.merge(heartbeatFlux, reactiveRedisTemplate
                        .listenToChannel(RedisChannel.WHISPER_TASK_CHANNEL + whisperSubmitVO.getTaskId())
                        .map(value -> {
                            log.info(value.toString());
                            return ServerSentEvent.<String>builder()
                                    .id(String.valueOf(System.currentTimeMillis()))
                                    .data(gson.toJson(value.getMessage()))
                                    .event("message")
                                    .build();
                        }))
                );
//        return Flux.merge(heartbeatFlux);
    }
}
