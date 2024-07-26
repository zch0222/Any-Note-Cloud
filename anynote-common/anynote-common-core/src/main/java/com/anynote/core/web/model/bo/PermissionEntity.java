package com.anynote.core.web.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity extends BaseEntity {

    /**
     * 权限(作者(创建者) 知识库管理员 同知识库用户 其它用户 匿名用户)
     */
    private String permissions;

    /**
     * 数据权限 1.自己可见 2.自己和管理员可见 3.知识库中所有人可见
     */
    private Integer dataScope;

    public PermissionEntity(String permissions, Integer dataScope, Long createBy, Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.permissions = permissions;
        this.dataScope = dataScope;
    }
}
