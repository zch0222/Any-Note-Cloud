package com.anynote.note.api;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.api.factory.RemoteKnowledgeBaseFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @GetMapping("bases/users/ids/{knowledgeBaseId}")
    public ResData<List<Long>> getKnowledgeBaseUserIds(@PathVariable("knowledgeBaseId") Long knowledgeBaseId,
                                                       @RequestHeader("from-source") String fromSource);

    @GetMapping("/bases/inner/{id}")
    public ResData<NoteKnowledgeBaseDTO> innerGetKnowledgeBaseById(@PathVariable("id") Long id,
                                                                   @RequestHeader("from-source") String fromSource);
}
