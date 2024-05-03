package com.anynote.common.rocketmq.tags;

/**
 * @author 称霸幼儿园
 */
public enum AIChatTagsEnum {

    CREATE_CONVERSATION("创建对话"),
    SAVE_MESSAGE("保存消息")
    ;

    private final String description;

    AIChatTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
