package com.anynote.common.rocketmq.tags;

/**
 * @author 称霸幼儿园
 */
public enum NoteTagsEnum {

    SUBMIT("提交任务"),
    DELETE_NOTE_INDEX("删除笔记索引"),
    GENERATOR_NOTE_INDEX("生成笔记索引"),
    GENERATE_NOTE_EDIT_LOG("生成笔记编辑记录"),
    SAVE_NOTE_FILE("记录笔记文件关联表"),

    NOTE_TASK_CREATED("创建笔记任务成功")
    ;

    private final String description;

    NoteTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
