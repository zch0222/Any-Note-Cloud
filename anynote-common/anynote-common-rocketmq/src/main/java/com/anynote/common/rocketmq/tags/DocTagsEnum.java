package com.anynote.common.rocketmq.tags;

/**
 * @author 称霸幼儿园
 */
public enum DocTagsEnum {


    RAG_INDEX("建立RAG索引"),
    TRANSLATE_DOC_NAME_TO_ENGLISH("翻译文档名称为英语");

    private final String description;

    DocTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
