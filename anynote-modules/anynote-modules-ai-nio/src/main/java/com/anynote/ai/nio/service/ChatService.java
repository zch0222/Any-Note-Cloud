package com.anynote.ai.nio.service;

import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.api.model.bo.ChatConversationUpdateParam;
import com.anynote.ai.api.model.dto.ChatCompletionsDTO;
import com.anynote.ai.api.model.dto.ChatConversationListDTO;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.vo.ChatCompletionsVO;

import com.anynote.ai.nio.model.vo.ChatConversationInfoVO;
import com.anynote.ai.nio.model.vo.ChatConversationVO;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface ChatService {

//    public List<ChatMessage> selectChatMessageList(Long conversationId);
//
//
//    public Mono<ChatConversationVO> getConversationById(ChatConversationQueryParam queryParam);
//
//    public ChatConversationPermissions getConversationPermissions(Long conversationId);

    public Mono<Long> authConversationPermissions(Long conversationId,
                                                     ChatConversationPermissions reqPermissions,
                                                     String accessToken);

//    public Mono<Boolean> createC

    public Mono<ChatConversationVO> getChatConversationById(ChatConversationQueryParam queryParam);

    public Flux<ChatCompletionsVO> chatCompletions(ChatCompletionsDTO chatCompletionsDTO);

    public Mono<PageBean<ChatConversationInfoVO>> getChatConversationList(ChatConversationListDTO chatConversationListDTO);

    public Flux<ChatCompletionsVO> authedChatCompletions(ChatCompletionsDTO chatCompletionsDTO);

    public Mono<String> updateChatConversation(ChatConversationUpdateParam updateParam);

}
