package com.anynote.note.model.dto;

import com.anynote.file.api.model.dto.CompleteUploadDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author 称霸幼儿园
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompleteDocUploadDTO extends CompleteUploadDTO {

    /**
     * 知识库id
     */
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    @NotNull(message = "文档名称不能为空")
    private String docName;





}
