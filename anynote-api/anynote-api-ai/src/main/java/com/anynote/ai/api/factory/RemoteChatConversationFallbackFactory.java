package com.anynote.ai.api.factory;

import com.anynote.ai.api.RemoteChatConversationService;
import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteChatConversationFallbackFactory implements FallbackFactory<RemoteChatConversationService> {

    @Override
    public RemoteChatConversationService create(Throwable cause) {
        return new RemoteChatConversationService() {
            @Override
            public ResData<ChatConversationPermissions> getChatConversationPermissions(Long id, String fromSource,
                                                                                       String accessToken) {
                return ResData.error(ResCode.BUSINESS_ERROR, "用户权限查询失败");
            }
        };
    }
}
