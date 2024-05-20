package com.anynote.note.model.vo;

import com.anynote.core.web.model.bo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoteListVO extends BaseEntity {

    private Long id;

    private String title;

    private Long noteTextId;

    private Long knowledgeBaseId;

    private Integer status;

    private Integer dataScope;

    private Integer deleted;

    private Date latestOperationTime;

    private Integer notePermissions;

    private String knowledgeBaseName;

}
