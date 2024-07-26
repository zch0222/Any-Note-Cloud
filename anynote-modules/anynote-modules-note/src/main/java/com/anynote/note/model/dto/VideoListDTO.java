package com.anynote.note.model.dto;

import com.anynote.core.web.model.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
public class VideoListDTO {

    /**
     * 文件夹id
     */
    @NotNull(message = "文件夹id不能为空")
    private Long folderId;

    /**
     * 知识库id
     */
    private Long knowledgeBaseId;

}
