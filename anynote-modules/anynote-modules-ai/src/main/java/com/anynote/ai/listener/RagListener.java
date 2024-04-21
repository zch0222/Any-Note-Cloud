package com.anynote.ai.listener;

import com.anynote.ai.api.model.po.RagLog;
import com.anynote.ai.service.RagLogService;
import com.anynote.common.rocketmq.tags.RagTagsEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${anynote.data.rocketmq.rag-topic}",
        consumerGroup = "${anynote.data.rocketmq.rag-group}", maxReconsumeTimes = 2)
public class RagListener implements RocketMQListener<MessageExt> {


    @Resource
    private RagLogService ragLogService;

    @Override
    public void onMessage(MessageExt messageExt) {
        RagTagsEnum ragTagsEnum = RagTagsEnum.valueOf(messageExt.getTags());
        Gson gson = new Gson();
        if (RagTagsEnum.SAVE_RAG_LOG.equals(ragTagsEnum)) {
            this.saveRagLog(gson.fromJson(new String(messageExt.getBody()), RagLog.class));
        }
    }

    private void saveRagLog(RagLog ragLog) {
        this.ragLogService.getBaseMapper().insert(ragLog);
    }
}
