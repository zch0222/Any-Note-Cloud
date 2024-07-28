package com.anynote.system.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.*;

import java.util.Date;
import java.util.Map;

/**
 * 权限规则表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPermissionRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 权限名称
     */
    private String permissionRuleName;

    /**
     * 要求的权限
     */
    private Integer requirePermission;

    /**
     * 数据库结构类型0.没有关联知识库 1. 1:1知识库id字段在实体表上 2. n:m知识库id字段在中间表上
     */
    private Integer knowledgeBaseAssociationType;

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
    private String permissionsFieldName;

    /**
     * 知识库id字段名
     */
    private String knowledgeBaseIdFieldName;

    /**
     * 实体知识库关联表名
     */
    private String associationTableName;

    @TableLogic
    @TableField("is_delete")
    private Integer deleted;

    /**
     * 删除时间戳
     */
    private Long deleteTime;

    @Builder
    public SysPermissionRule(Long id, String permissionRuleName, Integer requirePermission, Integer knowledgeBaseAssociationType,
                             String entityTableName, String entityIdFieldName, String permissionsFieldName, String knowledgeBaseIdFieldName,
                             String associationTableName, Integer deleted, Long deleteTime, Long createBy,
                             Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.permissionRuleName = permissionRuleName;
        this.requirePermission = requirePermission;
        this.knowledgeBaseAssociationType = knowledgeBaseAssociationType;
        this.entityTableName = entityTableName;
        this.entityIdFieldName = entityIdFieldName;
        this.permissionsFieldName = permissionsFieldName;
        this.knowledgeBaseIdFieldName = knowledgeBaseIdFieldName;
        this.associationTableName = associationTableName;
        this.deleted = deleted;
        this.deleteTime = deleteTime;
    }

}
