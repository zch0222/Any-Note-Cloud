package com.anynote.ai.nio.service;

import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.nio.model.vo.ChatConversationVO;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

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


}
