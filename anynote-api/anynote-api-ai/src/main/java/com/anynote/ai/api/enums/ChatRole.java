package com.anynote.ai.api.enums;

import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.StringUtils;

public enum ChatRole {

    USER(0),
    ASSISTANT(1),

    SYSTEM(2);

    private final int value;

    ChatRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChatRole parse(int value) {
        if (USER.getValue() == value) {
            return USER;
        }
        else if (ASSISTANT.getValue() == value) {
            return ASSISTANT;
        }
        else if (SYSTEM.getValue() == value) {
            return SYSTEM;
        }
        throw new BusinessException(StringUtils.format("chatRole={}为定义", value));
    }
}
