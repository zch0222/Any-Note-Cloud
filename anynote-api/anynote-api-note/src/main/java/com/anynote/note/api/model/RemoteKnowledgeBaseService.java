package com.anynote.note.api.model;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.api.model.factory.RemoteKnowledgeBaseFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author 称霸幼儿园
 */
@FeignClient(contextId = "remoteKnowledgeBaseService",
        value = ServiceNameConstants.NOTE_SERVICE,
        fallbackFactory = RemoteKnowledgeBaseFallbackFactory.class)
public interface RemoteKnowledgeBaseService {

    @GetMapping("bases/managerList")
    public ResData<PageBean<NoteKnowledgeBaseDTO>> getManagerKnowledgeBases(@RequestParam("page") Integer page,
                                                                            @RequestParam("pageSize") Integer pageSize,
                                                                            @RequestParam("type") Integer type,
                                                                            @RequestParam("status") Integer status,
                                                                            @RequestParam("organizationId") Long organizationId);
}
