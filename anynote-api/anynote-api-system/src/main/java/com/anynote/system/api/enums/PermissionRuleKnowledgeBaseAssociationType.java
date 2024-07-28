package com.anynote.system.api.enums;

public enum PermissionRuleKnowledgeBaseAssociationType {

    /**
     * 没有关联知识库
     */
    NOT_ASSOCIATED(0),

    /**
     * 一对一关联
     * 关联知识库id字段在实体表上
     */
    ONE_TO_ONE(1),

    /**
     * 多对多关联
     * 关联知识库id在中间表上
     */
    M_TO_N(2)
    ;

    private final int value;

    PermissionRuleKnowledgeBaseAssociationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
