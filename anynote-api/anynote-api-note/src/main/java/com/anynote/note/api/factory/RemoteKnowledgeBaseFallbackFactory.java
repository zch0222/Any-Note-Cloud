package com.anynote.note.api.factory;

import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author 称霸幼儿园
 */
@Slf4j
@Component
public class RemoteKnowledgeBaseFallbackFactory implements FallbackFactory<RemoteKnowledgeBaseService> {


    @Override
    public RemoteKnowledgeBaseService create(Throwable cause) {
        log.error("知识库服务调用失败: {}", cause.getMessage());
        return new RemoteKnowledgeBaseService() {

            @Override
            public ResData<PageBean<NoteKnowledgeBaseDTO>> getManagerKnowledgeBases(Integer page, Integer pageSize, Integer type, Integer status, Long organizationId) {
                return ResUtil.error(ResCode.INNER_NOTE_SERVICE_ERROR);
            }
        };
    }
}
