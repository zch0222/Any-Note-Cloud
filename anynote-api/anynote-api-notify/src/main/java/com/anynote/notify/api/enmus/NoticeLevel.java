package com.anynote.notify.api.enmus;

/**
 * 通知级别
 */
public enum NoticeLevel {

    LOW(0),

    MEDIUM(1),

    HIGH(2);

    private final int type;

    NoticeLevel(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
