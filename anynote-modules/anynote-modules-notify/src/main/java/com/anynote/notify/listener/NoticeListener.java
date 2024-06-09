package com.anynote.notify.listener;

import com.anynote.common.rocketmq.tags.NotifyTagsEnum;
import com.anynote.notify.api.model.po.Notice;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${anynote.data.rokcketmq.notify-topic}",
        consumerGroup = "${anynote.data.rocketmq.notify-group}", messageModel = MessageModel.CLUSTERING,
        selectorType = SelectorType.TAG, selectorExpression = "NOTICE")
public class NoticeListener implements RocketMQListener<MessageExt> {

    @Resource
    private Gson gson;


    @Override
    public void onMessage(MessageExt messageExt) {
        NotifyTagsEnum tag = NotifyTagsEnum.valueOf(messageExt.getTags());
        if (NotifyTagsEnum.NOTICE.equals(tag)) {

        }
    }

    private void publishNotice(Notice notice) {

    }
}
