package com.anynote.common.datascope.enums;

public enum PermissionEnum {
    /**
     * 可管理(编辑、分享、删除)
     */
    MANAGE(7),

    /**
     * 编辑、阅读
     */
    EDIT(6),

    /**
     * 阅读
     */
    READ(4),

    /**
     * 没有权限
     */
    NO(0);
    ;

    private final int value;

    PermissionEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
