package com.anynote.notify.listener;

import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.rocketmq.tags.NoteTagsEnum;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.RemoteNoteTaskService;
import com.anynote.note.api.model.bo.NoteTaskCreatedMessageBody;
import com.anynote.note.api.model.po.UserNoteTask;
import com.anynote.notify.api.enmus.NoticeLevel;
import com.anynote.notify.api.enmus.NoticeType;
import com.anynote.notify.api.model.bo.NoticePublishParam;
import com.anynote.notify.service.NoticeService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${anynote.data.rocketmq.note-topic}",
        consumerGroup = "${anynote.data.rocketmq.notify-note-group}", maxReconsumeTimes = 5,
        messageModel = MessageModel.CLUSTERING, selectorType = SelectorType.TAG,
        selectorExpression = "NOTE_TASK_CREATED")
public class NotifyNoteMessageListener implements RocketMQListener<MessageExt> {

    @Resource
    private Gson gson;

    @Resource
    private RemoteKnowledgeBaseService remoteKnowledgeBaseService;

    @Resource
    private RemoteNoteTaskService remoteNoteTaskService;

    @Resource
    private NoticeService noticeService;



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
//        List<Long> userIds = RemoteResDataUtil.getResData(remoteKnowledgeBaseService
//                .getKnowledgeBaseUserIds(body.getKnowledgeBaseId(), "inner"), "获取知识库用户id错误");

        List<UserNoteTask> userNoteTaskList = RemoteResDataUtil.getResData(remoteNoteTaskService
                .getTaskUsers(body.getNoteTaskId(), "inner"), "获取任务用户列表失败");
        Date now = new Date();
        NoticePublishParam noticePublishParam = NoticePublishParam.builder()
                .title("任务：" + body.getTaskName())
                .content("收到任务：" + body.getTaskName())
                .type(NoticeType.KNOWLEDGE_BASE.getType())
                .status(0)
                .level(NoticeLevel.MEDIUM.getLevel())
                .createTime(now)
                .createBy(0L)
                .updateTime(now)
                .updateBy(0L)
                .knowledgeBaseId(body.getKnowledgeBaseId())
                .userIdList(userNoteTaskList.stream()
                        .filter(userNoteTask -> userNoteTask.getPermissions() > 1)
                        .map(UserNoteTask::getUserId)
                        .collect(Collectors.toList()))
                .build();
        noticeService.publishNotice(noticePublishParam);

//        for (Long userId : userIds) {
//            String chanel = RedisChannel.NOTIFY_CHANNEL_USER + userId;
//            log.info(chanel);
//            stringRedisTemplate.convertAndSend(chanel, gson.toJson(body));
//        }
    }
}
