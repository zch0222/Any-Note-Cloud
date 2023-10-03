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
@TableName("n_note_task_submission_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTaskSubmissionRecord extends BaseEntity {
    private Long noteTaskId;

    private Long userId;

    private Long noteId;

    private Date submitTime;

    @TableLogic
    @TableField("is_delete")
    private Integer deleted;
}
