package com.anynote.common.datascope.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityPermissionQueryParam {

    /**
     * 实体id
     */
    private Long entityId;

    /**
     * 实体表名
     */
    private String entityTableName;

    /**
     * 实体id字段名
     */
    private String entityIdFieldName;

    /**
     * 权限字段名称
     */
    private String permissionFieldName;

    /**
     * 知识库id字段名
     */
    private String knowledgeBaseIdFieldName;
}
