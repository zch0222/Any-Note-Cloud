package com.anynote.notify.test;

import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.NotifyTagsEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Test {

//    @Resource(name = "rocketMQClientConfiguration")
//    private ClientConfiguration clientConfiguration;
//
//    @Resource(name = "rocketMQClientServiceProvider")
//    private ClientServiceProvider clientServiceProvider;

    @Resource
    private RocketMQProperties rocketMQProperties;

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


//    @Resource
//    private RocketMQTemplate rocketMQTemplate;

    @org.junit.jupiter.api.Test
    public void test() {

        String destination = rocketMQProperties.getNotifyGroup() + ":" + NotifyTagsEnum.getNoticeTag(2L);
//        rocketMQTemplate.asyncSend(destination, "TEST", RocketmqSendCallbackBuilder.commonCallback());

//        FilterExpression filterExpression = new FilterExpression(NotifyTagsEnum.getNoticeTag(2L), FilterExpressionType.TAG);
//        final Producer producer = ProducerSingleton.getInstance(topic);
//        Message message = clientServiceProvider.newMessageBuilder()
//                // Set topic for the current message.
//                .setTopic(rocketMQProperties.getNotifyGroup())
//                // Message secondary classifier of message besides topic.
//                .setTag(NotifyTagsEnum.getNoticeTag(2L))
//                // Key(s) of the message, another way to mark message besides message id.
//                .setKeys("yourMessageKey-1c151062f96e")
//                .setBody("TEST".getBytes())
//                .build();
//
//        try {
//            final SendReceipt sendReceipt = producer.send(message);
//            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
//        } catch (Throwable t) {
//            log.error("Failed to send message", t);
//        }
    }

    @org.junit.jupiter.api.Test
    void testRedisPublish() {
//        reactiveRedisTemplate.convertAndSend("NOTIFY:2", "Hello World!");
        Map<String, String> map = new HashMap<>();
        map.put("TEST", "Hello World!");
        stringRedisTemplate.convertAndSend(RedisChannel.NOTIFY_CHANNEL_USER + 2L, new Gson().toJson(map));
    }



}
