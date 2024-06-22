package com.anynote.ai.nio.listener;

import com.anynote.ai.nio.model.vo.WhisperTaskStatusVO;
import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.redis.model.bo.RedisMessage;
import com.anynote.common.redis.service.RedisService;
import com.anynote.common.rocketmq.tags.WhisperTagsEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${anynote.data.rocketmq.note-topic}",
        consumerGroup = "${anynote.data.rocketmq.whisper-group}", maxReconsumeTimes = 5,
        messageModel = MessageModel.CLUSTERING, selectorType = SelectorType.TAG,
        selectorExpression = "WHISPER_TASK_FINISHED | WHISPER_TASK_STATUS_UPDATE")
public class WhisperListener implements RocketMQListener<MessageExt> {

    @Resource
    private RedisService redisService;

    @Resource
    private Gson gson;

    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("TAG: " + messageExt.getTags());
        if (WhisperTagsEnum.WHISPER_TASK_FINISHED.equals(WhisperTagsEnum.valueOf(messageExt.getTags()))) {

        }
        else if (WhisperTagsEnum.WHISPER_TASK_STATUS_UPDATED.equals(WhisperTagsEnum.valueOf(messageExt.getTags()))) {
            String body = new String(messageExt.getBody());
            log.info("WHISPER_TASK_STATUS_UPDATED" + body);
            this.onWhisperTaskStatusUpdate(gson.fromJson(body, WhisperTaskStatusVO.class));
        }
    }

    private void onWhisperTaskStatusUpdate(WhisperTaskStatusVO whisperTaskStatusVO) {
        RedisMessage[] redisMessages = {
                RedisMessage.builder()
                        .channel(RedisChannel.WHISPER_TASK_CHANNEL + whisperTaskStatusVO.getTaskId())
                        .message(gson.toJson(whisperTaskStatusVO))
                        .build()
        };
        redisService.batchPublish(Arrays.asList(redisMessages));
    }
}
