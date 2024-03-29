package com.anynote.note.model.bo;

import lombok.*;

/**
 * @author 称霸幼儿园
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoteTaskQueryParam extends KnowledgeBaseQueryParam {

    /**
     * 笔记任务id
     */
    private Long noteTaskId;

    /**
     * 需要提交的用户id
     */
    private Long submitUserId;

    /**
     * 任务状态 0.进行中 1.已结束
     */
    private Integer taskStatus;

    /**
     * 提交状态 0.已提交 1.未提交
     */
    private Integer submissionStatus;



    @Builder(builderMethodName = "NoteTaskQueryParamBuilder")
    public NoteTaskQueryParam(Long knowledgeBaseId, String knowledgeBaseName,
                              Long organizationId, Integer page, Integer pageSize,
                              Long noteTaskId, Long submitUserId, Integer submissionStatus,
                              Integer taskStatus) {
        this.setId(knowledgeBaseId);
        this.setName(knowledgeBaseName);
        this.setOrganizationId(organizationId);
        this.setPage(page);
        this.setPageSize(pageSize);
        this.submissionStatus = submissionStatus;
        this.taskStatus = taskStatus;
        this.noteTaskId = noteTaskId;
        this.submitUserId = submitUserId;
    }

}
