package com.anynote.ai.api;

import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.factory.RemoteChatConversationFallbackFactory;
import com.anynote.ai.api.factory.RemoteRagFallbackFactory;
import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(contextId = "remoteChatConversationService",
        value = ServiceNameConstants.AI_SERVICE, fallbackFactory = RemoteChatConversationFallbackFactory.class)
public interface RemoteChatConversationService {

    @GetMapping("/chat/conversations/{id}/permissions")
    public ResData<ChatConversationPermissions> getChatConversationPermissions(@PathVariable("id") Long id,
                                                                               @RequestHeader("from-source") String fromSource,
                                                                               @RequestHeader("accessToken") String accessToken);


}
