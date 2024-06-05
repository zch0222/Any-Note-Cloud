package com.anynote.ai.datascope.annotation;

import com.anynote.ai.api.enums.ChatConversationPermissions;

import java.lang.annotation.*;

/**
 * 需要的对话权限
 * @author 称霸幼儿园
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresChatConversationPermissions {

    ChatConversationPermissions value();
}
