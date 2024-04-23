package com.anynote.ai.api;

import com.anynote.ai.api.exception.RagLimitException;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.bo.RagFileQueryRes;
import com.anynote.ai.api.model.po.RagLog;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.redis.service.RedisService;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.RagTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.DateUtils;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.system.api.model.bo.LoginUser;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

@Component
@Slf4j
public class RagService {

    @Resource
    private WebClient webClient;

    @Resource
    private ConfigService configService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RocketMQProperties rocketMQProperties;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

//    @Resource
//    private TokenUtil tokenUtil;

    private final String RAG_LIMIT_LUA_SCRIPT = "local userId = ARGV[1]\n" +
            "local date = ARGV[2]\n" +
            "local ragCountKey = 'RAG:COUNT:' .. string.sub(date, 2, -2) .. ':' .. userId\n" +
            "local maxCount = ARGV[3]\n" +
            "local isExists = redis.call('EXISTS', ragCountKey)\n" +
            "local currentCount = 0\n" +
            "if isExists == 1 then\n" +
            "    currentCount = tonumber(redis.call('get', ragCountKey))\n" +
            "else\n" +
            "    redis.call('SET', ragCountKey, 0)\n" +
            "end\n" +
            "if(currentCount >= tonumber(ARGV[3])) then\n" +
            "    return '1'\n" +
            "end\n" +
            "redis.call('incr', ragCountKey)\n" +
            "return '0'";

    private void ragLimitCheck(Long userId) {
//        LoginUser loginUser = tokenUtil.getLoginUser();
        DefaultRedisScript<String> script = new DefaultRedisScript<>(RAG_LIMIT_LUA_SCRIPT);
        script.setResultType(String.class);
        Integer res = (Integer) redisTemplate.execute(script, Collections.emptyList(), userId, DateUtils.getCurrentDateString(),
                configService.getRagMaxDayCount());
        if (StringUtils.isNotNull(res) && res == 0) {
            return;
        }
        throw new RagLimitException();
    }

    public void query(Long userId, RagFileQueryReq req, Function<RagFileQueryRes, Void> callback) {
        this.ragLimitCheck(userId);
        Date date = new Date();
        RagLog ragLog = RagLog.builder()
                .fileHash(req.getFile_hash())
                .fileName(req.getFile_name())
                .author(req.getAuthor())
                .category(req.getCategory())
                .description(req.getDescription())
                .prompt(req.getPrompt())
                .startTime(date)
                .deleted(0)
                .createTime(date)
                .updateTime(date)
                .updateBy(userId)
                .createBy(userId)
                .build();
        String aiServerAddress = configService.getAIServerAddress();
        Gson gson = new Gson();
        Flux<RagFileQueryRes> resFlux = webClient.post()
                .uri(aiServerAddress + "/api/rag/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(gson.toJson(req)))
                .exchangeToFlux(res -> {
                    if (res.statusCode().isError()) {
                        throw new BusinessException("Rag查询失败");
                    }
                    else {
                        return res.bodyToFlux(RagFileQueryRes.class);
                    }
                });

        CountDownLatch latch = new CountDownLatch(1);
        RagFileQueryRes[] ragFileQueryRes = {null};
        resFlux.subscribe(
                value -> {
                    ragFileQueryRes[0] = value;
                    callback.apply(value);
                },
                error -> {
                    error.printStackTrace();
                    latch.countDown();
                },
                () -> {
                    if (ragFileQueryRes[0].getStatus().equals("finished")) {
                        ragLog.setResult(0);
                    }
                    else {
                        ragLog.setResult(1);
                    }
                    log.info(gson.toJson(ragLog));
                    log.info(gson.toJson(ragFileQueryRes[0]));
                    ragLog.setMessage(ragFileQueryRes[0].getResult());
                    ragLog.setEndTime(new Date());
                    String destination = rocketMQProperties.getRagTopic() + ":" + RagTagsEnum.SAVE_RAG_LOG.name();
                    rocketMQTemplate.asyncSend(destination, gson.toJson(ragLog),
                            RocketmqSendCallbackBuilder.commonCallback());
                    latch.countDown();
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }




}
