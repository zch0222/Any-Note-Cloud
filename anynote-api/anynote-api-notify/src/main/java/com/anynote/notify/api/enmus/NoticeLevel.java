package com.anynote.notify.api.enmus;

/**
 * 通知级别
 */
public enum NoticeLevel {

    LOW(0),

    MEDIUM(1),

    HIGH(2);

    private final int level;

    NoticeLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
