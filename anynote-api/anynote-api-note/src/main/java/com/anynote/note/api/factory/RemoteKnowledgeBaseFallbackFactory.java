package com.anynote.note.api.factory;

import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.model.dto.GetUserKnowledgeBaseListDTO;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.api.model.po.UserKnowledgeBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

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

            @Override
            public ResData<List<Long>> getKnowledgeBaseUserIds(Long knowledgeBaseId, String fromSource) {
                return ResUtil.error(ResCode.INNER_NOTE_SERVICE_ERROR);
            }

            @Override
            public ResData<NoteKnowledgeBaseDTO> innerGetKnowledgeBaseById(Long id, String fromSource) {
                return ResUtil.error(ResCode.INNER_NOTE_SERVICE_ERROR);
            }

            @Override
            public ResData<List<UserKnowledgeBase>> getUserKnowledgeBaseList(GetUserKnowledgeBaseListDTO getUserKnowledgeBaseListDTO,
                                                                             String fromSource) {
                return ResData.error(ResCode.INNER_NOTE_SERVICE_ERROR);
            }
        };
    }
}
