package com.anynote.notify.listener;

import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.rocketmq.tags.NoteTagsEnum;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.model.bo.NoteTaskCreatedMessageBody;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${anynote.data.rocketmq.note-topic}",
        consumerGroup = "${anynote.data.rocketmq.notify-note-group}", maxReconsumeTimes = 5)
public class NotifyNoteMessageListener implements RocketMQListener<MessageExt> {

    @Resource
    private Gson gson;

    @Resource
    private RemoteKnowledgeBaseService remoteKnowledgeBaseService;

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public void onMessage(MessageExt messageExt) {
        NoteTagsEnum tag = NoteTagsEnum.valueOf(messageExt.getTags());
        log.info(tag.toString());
        log.info(gson.toJson(messageExt));
        if (NoteTagsEnum.NOTE_TASK_CREATED.equals(tag)) {
            log.info(new String(messageExt.getBody()));
            NoteTaskCreatedMessageBody body = gson.fromJson(new String(messageExt.getBody()),
                    NoteTaskCreatedMessageBody.class);
            publishNoteTaskCreateNotice(body);
        }
    }

    private void publishNoteTaskCreateNotice(NoteTaskCreatedMessageBody body) {
        List<Long> userIds = RemoteResDataUtil.getResData(remoteKnowledgeBaseService
                .getKnowledgeBaseUserIds(body.getKnowledgeBaseId(), "inner"), "获取知识库用户id错误");
        for (Long userId : userIds) {
            String chanel = RedisChannel.NOTIFY_CHANNEL_USER + userId;
            log.info(chanel);
            reactiveRedisTemplate.convertAndSend(chanel, gson.toJson(body));
        }
    }
}
