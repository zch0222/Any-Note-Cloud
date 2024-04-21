package com.anynote.common.rocketmq.tags;

/**
 * @author 称霸幼儿园
 */
public enum RagTagsEnum {


    SAVE_RAG_LOG("保存RAG日志");

    private final String description;

    RagTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
