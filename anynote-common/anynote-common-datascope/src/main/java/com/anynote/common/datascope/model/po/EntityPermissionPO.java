package com.anynote.common.datascope.model.po;

import lombok.Data;

import java.util.List;

@Data
public class EntityPermissionPO {

    /**
     * 实体id
     */
    private Long entityId;

    /**
     * 创建者ID
     */
    private Long createBy;

    /**
     * 权限字段
     */
    private String permissions;

    /**
     * 实体所属的知识库id列表
     */
    private List<Long> knowledgeBaseIds;
}
