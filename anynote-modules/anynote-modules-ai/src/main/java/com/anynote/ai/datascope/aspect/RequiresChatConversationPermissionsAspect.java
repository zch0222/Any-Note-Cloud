package com.anynote.ai.datascope.aspect;

import com.anynote.ai.datascope.annotation.RequiresChatConversationPermissions;
import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.service.ChatService;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@Order(1)
public class RequiresChatConversationPermissionsAspect {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private ChatService chatService;

    public final static String CHAT_CONVERSATION_PERMISSIONS = "chatConversationPermissions";


    @Before("@annotation(requiresChatConversationPermissions)")
    public void doBefore(JoinPoint joinPoint, RequiresChatConversationPermissions requiresChatConversationPermissions) {
        ChatConversationQueryParam queryParam = getParam(joinPoint);
        if (StringUtils.isNull(queryParam)) {
            throw new BusinessException("未知异常，请联系管理员");
        }
        authPermissions(queryParam, requiresChatConversationPermissions.value());

    }

    private void authPermissions(ChatConversationQueryParam queryParam, ChatConversationPermissions reqPermissions) {
        if (StringUtils.isNull(queryParam.getConversationId())) {
            return;
        }
        ChatConversationPermissions permissions = chatService.getConversationPermissions(queryParam.getConversationId());
        if (permissions.getValue() < reqPermissions.getValue()) {
            throw new AuthException("没有权限执行操作");
        }
    }


    private ChatConversationQueryParam getParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof ChatConversationQueryParam)) {
            return null;
        }
        return (ChatConversationQueryParam) params;
    }

}
