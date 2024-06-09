package com.anynote.notify.api.enmus;

/**
 * Notice 类型
 */
public enum NoticeType {

    /**
     * 个人
     */
    PERSONAL(0),

    /**
     * 知识库
     */
    KNOWLEDGE_BASE(1),

    /**
     * 系统
     */
    SYSTEM(2),;

    private final int type;

    NoticeType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
