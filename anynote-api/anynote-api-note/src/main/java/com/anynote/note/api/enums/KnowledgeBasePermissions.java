package com.anynote.note.api.enums;

/**
 * 知识库数据权限类型
 * @author 称霸幼儿园
 */
public enum KnowledgeBasePermissions {

    /**
     * 可管理(管理员) (查看、编辑)
     */
    MANAGE(1),

    /**
     * 可编辑(查看)
     */
    EDIT(2),

    /**
     * 可阅读
     */
    READ(3),

    /**
     * 无权限
     */
    NO(4);

    private final int value;

    KnowledgeBasePermissions(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
