package com.anynote.common.datascope.enums;

public enum PermissionRuleDatabaseStructureType {

    /**
     * 没有关联知识库
     */
    NOT_ASSOCIATED(0),


    ;

    private final int value;

    PermissionRuleDatabaseStructureType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

