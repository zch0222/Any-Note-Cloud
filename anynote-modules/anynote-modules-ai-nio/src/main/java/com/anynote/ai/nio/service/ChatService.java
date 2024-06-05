package com.anynote.ai.nio.service;

import com.anynote.ai.api.enums.ChatConversationPermissions;
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


}
