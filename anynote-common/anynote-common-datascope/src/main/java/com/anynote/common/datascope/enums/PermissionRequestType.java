package com.anynote.common.datascope.enums;

public enum PermissionRequestType {

    /**
     * 非SSE
     */
    NORMAL(0),

    /**
     * SSE
     */
    SSE(1)
    ;

    public final int value;

    PermissionRequestType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
