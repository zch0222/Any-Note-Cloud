package com.anynote.notify.api.enmus;

public enum UserNoticeStatus {

    /**
     * 未读
     */
    UNREAD(0),

    /**
     * 已读
     */
    READ(1);

    private final int type;

    UserNoticeStatus(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
