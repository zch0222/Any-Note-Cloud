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

/**
 * @author 称霸幼儿园
 */
@TableName("n_note_task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTask extends BaseEntity {
    /**
     * 笔记任务id
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    private Date startTime;

    private Date endTime;

    private Long knowledgeBaseId;

    private Integer status;

    @TableLogic
    @TableField("is_delete")
    private Integer deleted;
}
