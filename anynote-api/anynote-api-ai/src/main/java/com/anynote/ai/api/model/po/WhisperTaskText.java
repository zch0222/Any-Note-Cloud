package com.anynote.ai.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.Map;

@Data
@TableName("a_whisper_task_text")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WhisperTaskText extends BaseEntity {

    private Long id;

    private Long whisperTaskId;

    private String whisperText;

    @TableField("is_delete")
    @TableLogic
    private Integer deleted;

    @Builder
    public WhisperTaskText(Long id, Long whisperTaskId, String whisperText, Integer deleted, Long createBy,
                           Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.whisperTaskId = whisperTaskId;
        this.whisperText = whisperText;
        this.deleted = deleted;
    }
}
