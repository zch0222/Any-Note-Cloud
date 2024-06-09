package com.anynote.notify.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.Map;

/**
 * @author 称霸幼儿园
 */
@EqualsAndHashCode(callSuper = true)
@TableName("ntc_notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    private Integer level;

    @TableField("is_delete")
    @TableLogic
    private Integer deleted;

    @Builder
    public Notice(Long id, String title, String content, Integer type, Integer status, Integer deleted, Long createBy,
                  Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.deleted = deleted;
    }

}
