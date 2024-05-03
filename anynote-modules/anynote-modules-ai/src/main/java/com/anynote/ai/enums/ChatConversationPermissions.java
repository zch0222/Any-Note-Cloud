package com.anynote.ai.enums;

import com.anynote.core.exception.BusinessException;

public enum ChatConversationPermissions {

    /**
     * 可管理(编辑、分享、删除)
     */
    MANAGE(7),

    /**
     * 编辑、阅读
     */
    EDIT(6),

    /**
     * 阅读
     */
    READ(4),

    /**
     * 没有权限
     */
    NO(0);

    private final int value;

    ChatConversationPermissions(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChatConversationPermissions parse(int value) {
        if (7 == value) {
            return ChatConversationPermissions.MANAGE;
        }
        else if (6 == value) {
            return ChatConversationPermissions.EDIT;
        }
        else if (4 == value) {
            return ChatConversationPermissions.READ;
        }
        else if (0 == value) {
            return ChatConversationPermissions.NO;
        }
        throw new BusinessException("文档权限错误");
    }

}
