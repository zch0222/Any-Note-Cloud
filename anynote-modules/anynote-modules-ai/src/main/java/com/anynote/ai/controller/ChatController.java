package com.anynote.ai.controller;

import com.anynote.ai.enums.ChatType;
import com.anynote.ai.model.bo.ChatConversationQueryParam;
import com.anynote.ai.model.dto.ChatConversationListDTO;
import com.anynote.ai.model.vo.ChatConversationInfoVO;
import com.anynote.ai.model.vo.ChatConversationVO;
import com.anynote.ai.service.ChatService;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("chat")

public class ChatController {

    @Resource
    private ChatService chatService;

    @GetMapping("conversations/{id}")
    public ResData<ChatConversationVO> getChatConversationById(@Validated @NotNull(message = "知识库id不能为空")
                                                                   @PathVariable("id") Long id) {
        return ResUtil.success(chatService.getConversationById(ChatConversationQueryParam.builder()
                        .conversationId(id).build()));
    }

    @GetMapping("conversations")
    public ResData<PageBean<ChatConversationInfoVO>> getConversationList(@Validated
                                                                             ChatConversationListDTO chatConversationListDTO) {
        return ResUtil.success(chatService.getConversationList(ChatConversationQueryParam.builder()
                        .type(ChatType.DOC_RAG.getValue())
                        .docId(chatConversationListDTO.getDocId())
                        .page(chatConversationListDTO.getPage())
                        .pageSize(chatConversationListDTO.getPageSize())
                .build()));
    }
}
