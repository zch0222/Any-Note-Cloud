package com.anynote.ai.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("a_ali_green_log")
public class AliGreenLog extends BaseEntity {

    /**
     * Record id
     */
    private Long id;

    /**
     * Service type
     */
    private String service;

    /**
     * Content
     */
    private String content;

    /**
     * Response
     */
    private String response;

    /**
     * Status, 0 for normal, 1 for exception
     */
    private Integer status;

    /**
     * Delete flag, 0 for not deleted, 1 for deleted
     */
    @TableField("is_delete")
    @TableLogic
    private Integer deleted;

    @Builder
    public AliGreenLog(Long id, String service, String content, String response, Integer status,
                       Integer deleted, Long createBy,
                       Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.service = service;
        this.content = content;
        this.response = response;
        this.status = status;
        this.deleted = deleted;
    }

}
