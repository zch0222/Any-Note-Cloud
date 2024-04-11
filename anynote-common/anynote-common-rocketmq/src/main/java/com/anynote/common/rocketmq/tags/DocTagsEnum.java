package com.anynote.common.rocketmq.tags;

/**
 * @author 称霸幼儿园
 */
public enum DocTagsEnum {


    RAG_INDEX("建立RAG索引");

    private final String description;

    DocTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
