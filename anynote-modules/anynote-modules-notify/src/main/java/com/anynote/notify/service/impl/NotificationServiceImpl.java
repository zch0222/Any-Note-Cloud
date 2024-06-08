package com.anynote.notify.service.impl;

import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.NotifyTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.notify.model.dto.NoticeDTO;
import com.anynote.notify.service.NotificationService;
import com.anynote.system.api.model.bo.LoginUser;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private Gson gson;

//    @Resource(name = "rocketMQClientConfiguration")
//    private ClientConfiguration clientConfiguration;
//
//    @Resource(name = "rocketMQClientServiceProvider")
//    private ClientServiceProvider clientServiceProvider;

    @Resource
    private RocketMQProperties rocketMQProperties;



    @Override
    public Flux<ServerSentEvent<String>> notice(NoticeDTO noticeDTO, String accessToken){
//        try {
//            LoginUser loginUser = tokenUtil.getLoginUser(accessToken);
//            Duration awaitDuration = Duration.ofSeconds(30);
//            FilterExpression filterExpression = new FilterExpression(NotifyTagsEnum.getNoticeTag(loginUser.getUserId()), FilterExpressionType.TAG);
//
//
//
//            return Mono.fromCallable(() -> {
//                AtomicReference<String> res = new AtomicReference<>();
//                log.info("create clientServiceProvider");
//                PushConsumer consumer = clientServiceProvider.newPushConsumerBuilder()
//                        .setClientConfiguration(clientConfiguration)
//                        // Set the consumer group name.
//                        .setConsumerGroup(rocketMQProperties.getNotifyGroup() + "-" + loginUser.getUserId())
//                        // set await duration for long-polling.
//                        // Set the subscription for the consumer.
//                        .setSubscriptionExpressions(Collections.singletonMap(rocketMQProperties.getNotifyGroup(), filterExpression))
//                        .setMessageListener(messageView -> {
//                            // Handle the received message and return consume result.
//                            log.info("Consume message={}", messageView);
//                            res.set(messageView.getBody().toString());
//                            return ConsumeResult.SUCCESS;
//                        })
//                        .build();
//                consumer.close();
//                log.info("res = {}", res.get());
//                return ServerSentEvent.<String>builder()
//                        .id(new Date().toString())
//                        .data(res.get())
//                        .event("message")
//                        .build();
//            }).publishOn(Schedulers.boundedElastic()).repeat().doFinally(signal -> {
//                log.info("FINALLY");
//            });
//        } catch (Exception e) {
//            log.error("error", e);
//            e.printStackTrace();
//            throw e;
//        }
 //                .map(value -> ServerSentEvent.<String>builder()
//                        .id(new Date().toString())
//                        .data(gson.toJson(ResUtil.success(loginUser.getRole())))
//                        .event("message")
//                        .build());
        return null;
    }
}
