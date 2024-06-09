package com.anynote.common.rocketmq.tags;

/**
 *
 * @author 称霸幼儿园
 */
public enum NoteTaskTagsEnum {
    SUBMIT("提交任务"),

    INSERT_HISTORY("插入操作历史"),


    ;

    private final String description;

    NoteTaskTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
