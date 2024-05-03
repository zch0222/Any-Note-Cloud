package com.anynote.ai.listener;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.service.ChatConversationService;
import com.anynote.common.rocketmq.tags.AIChatTagsEnum;
import com.anynote.common.rocketmq.tags.RagTagsEnum;
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
        AIChatTagsEnum aiChatTagsEnum = AIChatTagsEnum.valueOf(messageExt.getTags());
        Gson gson = new Gson();
        if (AIChatTagsEnum.CREATE_CONVERSATION.equals(aiChatTagsEnum)) {
            this.createChatConversation(gson.fromJson(new String(messageExt.getBody()), ChatConversation.class));
        }
    }

    private void createChatConversation(ChatConversation chatConversation) {
        this.chatConversationService.getBaseMapper().insert(chatConversation);
    }


}
