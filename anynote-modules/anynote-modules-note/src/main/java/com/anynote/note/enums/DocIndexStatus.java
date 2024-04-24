package com.anynote.note.enums;

public enum DocIndexStatus {
    /**
     * 索引成功
     */
    INDEXED(0),

    INDEXING(1),

    FAILED(2),

    NOT_INDEXED(3)
    ;

    private final int value;

    DocIndexStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
