package com.anynote.note.listener;

import com.anynote.ai.api.RemoteRagService;
import com.anynote.ai.api.RemoteTranslateService;
import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.dto.TranslateTextDTO;
import com.anynote.common.rocketmq.tags.DocTagsEnum;
import com.anynote.common.rocketmq.tags.NoteTagsEnum;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.file.api.RemoteFileService;
import com.anynote.file.api.model.po.FilePO;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.enums.DocIndexStatus;
import com.anynote.note.enums.DocType;
import com.anynote.note.service.DocService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private RemoteTranslateService remoteTranslateService;


    @Override
    public void onMessage(MessageExt messageExt) {
        DocTagsEnum docTagsEnum = DocTagsEnum.valueOf(messageExt.getTags());
        if (DocTagsEnum.RAG_INDEX.equals(docTagsEnum)) {
            Long docId = Long.valueOf(new String(messageExt.getBody()));
            log.info("建立文档RAG索引, 文档ID: " + docId);
            indexDoc(docId);
        }
        else if (DocTagsEnum.TRANSLATE_DOC_NAME_TO_ENGLISH.equals(DocTagsEnum.valueOf(messageExt.getTags()))) {
            Long docId = Long.valueOf(new String(messageExt.getBody()));
            log.info("翻译文档名称为英文，文档ID: " + docId);
            translateDocName2English(docId);
        }
    }

    private void indexDoc(Long docId) {
        Doc doc = docService.getBaseMapper().selectById(docId);
        doc.setIndexStatus(DocIndexStatus.INDEXING.getValue());
        docService.getBaseMapper().updateById(doc);
        FilePO filePO = RemoteResDataUtil.getResData(remoteFileService.getFileById(doc.getFileId(), "inner"),
                "获取文件信息失败");
        try {
            RemoteResDataUtil.getResData(remoteRagService.indexFile(RagFileIndexReq.builder()
                    .file_path(filePO.getUrl()).build(), "inner"), "索引建立失败");
            doc.setIndexStatus(DocIndexStatus.INDEXED.getValue());
        } catch (Exception e) {
            log.error("建立索引失败");
            doc.setIndexStatus(DocIndexStatus.FAILED.getValue());
        }
        docService.getBaseMapper().updateById(doc);
    }

    private void translateDocName2English(Long docId) {
        Doc doc = docService.getBaseMapper().selectById(docId);
        List<String> name = new ArrayList<>(1);
        name.add(doc.getName());
        String englishName = RemoteResDataUtil.getResData(remoteTranslateService.translateText(TranslateTextDTO.builder().text(name).targetLang("EN").build(),
                "inner"), "翻译失败").get(0).getText();
        doc.setEnglishName(englishName);
        docService.getBaseMapper().updateById(doc);
    }
}
