package com.anynote.note.api.model.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class NoteTaskCreatedMessageBody {

    private Long noteTaskId;

    /**
     * 任务名称
     */
    private String taskName;

    private Date startTime;

    private Date endTime;

    private Long knowledgeBaseId;

    private Integer status;

    private String taskDescribe;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;

    private String remark;

}
