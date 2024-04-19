package com.anynote.note.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 文档
 * @author 称霸幼儿园
 */
@Data
@TableName("n_doc")
@NoArgsConstructor
@AllArgsConstructor
public class Doc extends BaseEntity {

    /**
     * 文档id id
     */
    private Long id;

    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 英文名
     */
    private String englishName;

    /**
     * 知识库id
     */
    private Long knowledgeBaseId;

    /**
     * 文档类型 0. PDF
     */
    private Integer type;

    /**
     * 数据权限 1.自己可见 2.自己和管理员可见 3.知识库中所有人可见
     */
    private Integer dataScope;

    /**
     * 权限(作者(创建者) 知识库管理员 同知识库用户 其它用户 匿名用户)
     */
    private String permissions;

    /**
     * 删除标记 0.正常 1.删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer deleted;

    @Builder
    public Doc(Long id, Long fileId, String name, Long knowledgeBaseId, Integer type, Integer dataScope, String permissions, Integer deleted,
               Long createBy, Date createTime, Long updateBy, String englishName,
               Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.fileId = fileId;
        this.name = name;
        this.knowledgeBaseId = knowledgeBaseId;
        this.type = type;
        this.dataScope = dataScope;
        this.permissions = permissions;
        this.deleted = deleted;
        this.englishName = englishName;
    }


}
