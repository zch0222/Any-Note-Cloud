package com.anynote.ai.controller;

import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.dto.ConversationCreateDTO;
import com.anynote.ai.enums.ChatType;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.model.bo.ChatConversationCreateParam;
import com.anynote.ai.model.bo.ChatConversationUpdateParam;
import com.anynote.ai.model.dto.ChatConversationListDTO;
import com.anynote.ai.api.model.dto.ChatConversationUpdateDTO;
import com.anynote.ai.model.vo.ChatConversationInfoVO;
import com.anynote.ai.model.vo.ChatConversationVO;
import com.anynote.ai.service.ChatService;
import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @InnerAuth
    @PostMapping("conversations")
    public ResData<Long> createConversation(@Validated @RequestBody ConversationCreateDTO conversationCreateDTO) {
        int[] test = {1};
        int length = test.length;
        test[1] = 7;
        return ResUtil.success(chatService.createConversation(ChatConversationCreateParam
                .ChatConversationCreateParamBuilder()
                .title(conversationCreateDTO.getTitle())
                .type(conversationCreateDTO.getType())
                .docId(conversationCreateDTO.getDocId()).build()));
    }

    @InnerAuth
    @GetMapping("conversations/{id}/permissions")
    public ResData<ChatConversationPermissions> getChatConversationPermissions(@PathVariable("id") Long id) {
        return ResUtil.success(chatService.getConversationPermissions(id));
    }


    @PatchMapping("conversations/{id}")
    public ResData<String> updateChatConversation(@PathVariable("id") Long id,
                                                  @Validated @RequestBody ChatConversationUpdateDTO chatConversationUpdateDTO) {
        return ResUtil.success(chatService.updateChatConversation(ChatConversationUpdateParam
                .ChatConversationUpdateParamBuilder()
                .conversationId(id).title(chatConversationUpdateDTO.getTitle()).build()));
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
