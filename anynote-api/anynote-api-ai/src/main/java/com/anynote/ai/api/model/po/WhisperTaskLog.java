package com.anynote.ai.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.Map;


@Data
@TableName("a_whisper_task_log")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WhisperTaskLog extends BaseEntity {
    /**
     * Whisper 任务日志id
     */
    private Long id;

    /**
     * 任务id
     */
    private Long whisperTaskId;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 用户id
     */
    private Long userId;

    @TableField("is_delete")
    @TableLogic
    private Integer deleted;

    @Builder
    public WhisperTaskLog(Long id, Long whisperTaskId, String taskStatus, Long userId, Integer deleted, Long createBy,
                          Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.whisperTaskId = whisperTaskId;
        this.taskStatus = taskStatus;
        this.userId = userId;
        this.deleted = deleted;


    }
}
