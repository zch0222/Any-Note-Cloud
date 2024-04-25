package com.anynote.ai.listener;

import com.anynote.ai.service.ChatConversationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${anynote.data.rocketmq.ai-chat-topic}",
        consumerGroup = "${anynote.data.rocketmq.ai-chat-group}", maxReconsumeTimes = 2)
public class ChatListener implements RocketMQListener<MessageExt> {

    @Resource
    private ChatConversationService chatConversationService;

    @Override
    public void onMessage(MessageExt messageExt) {

    }


}
