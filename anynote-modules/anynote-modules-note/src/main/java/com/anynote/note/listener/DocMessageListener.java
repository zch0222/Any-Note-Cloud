package com.anynote.note.listener;

import com.anynote.ai.api.RemoteRagService;
import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.common.rocketmq.tags.DocTagsEnum;
import com.anynote.common.rocketmq.tags.NoteTagsEnum;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.file.api.RemoteFileService;
import com.anynote.file.api.model.po.FilePO;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.service.DocService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.ByteBuffer;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${anynote.data.rocketmq.doc-topic}",
        consumerGroup = "${anynote.data.rocketmq.doc-group}", maxReconsumeTimes = 2)
public class DocMessageListener implements RocketMQListener<MessageExt> {

    @Resource
    private RemoteRagService remoteRagService;

    @Resource
    private RemoteFileService remoteFileService;

    @Resource
    private DocService docService;


    @Override
    public void onMessage(MessageExt messageExt) {
        DocTagsEnum docTagsEnum = DocTagsEnum.valueOf(messageExt.getTags());
        if (DocTagsEnum.RAG_INDEX.equals(docTagsEnum)) {
            Long docId = Long.valueOf(new String(messageExt.getBody()));
            log.info("建立文档RAG索引, 文档ID: " + docId);
            indexDoc(docId);
        }
    }

    private void indexDoc(Long docId) {
        Doc doc = docService.getBaseMapper().selectById(docId);
        FilePO filePO = RemoteResDataUtil.getResData(remoteFileService.getFileById(doc.getFileId(), "inner"),
                "获取文件信息失败");
        RemoteResDataUtil.getResData(remoteRagService.indexFile(RagFileIndexReq.builder()
                .file_path(filePO.getUrl()).build(), "inner"), "索引建立失败");
    }
}
