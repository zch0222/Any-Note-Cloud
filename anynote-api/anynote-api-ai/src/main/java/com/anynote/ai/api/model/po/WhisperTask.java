package com.anynote.ai.api.model.po;

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
@Data
@TableName("a_whisper_task")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class WhisperTask extends BaseEntity {

    /**
     * whisper 任务id
     */
    private Long id;

    /**
     * task uuid python端生成
     */
    private String taskId;

    /**
     * srt文件地址
     */
    private String srtUrl;

    /**
     * txt文件地址
     */
    private String txtUrl;

    /**
     * 任务状态
     * 任务状态0. starting, 1.loading model, 2.downloading, 3.running, 4.finished
     */
    private Integer taskStatus;

    @TableField("is_delete")
    @TableLogic
    private Integer deleted;

    @Builder
    public WhisperTask(Long id, String taskId, String srtUrl, String txtUrl, Integer taskStatus, Integer deleted, Long createBy,
                       Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.taskId = taskId;
        this.srtUrl = srtUrl;
        this.txtUrl = txtUrl;
        this.taskStatus = taskStatus;
        this.deleted = deleted;
    }
}
