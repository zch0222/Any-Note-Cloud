package com.anynote.note.model.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class NoteImageUploadTempLinkDTO {

    /**
     * 笔记id
     */
    @NotNull(message = "笔记id不能为空")
    private Long noteId;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    @Pattern(regexp = ".*\\.(?i)(jpeg|png|gif|jpg)$", message = "文件类型错误")
    private String fileName;

    /**
     * 图片类型
     */
    @NotBlank(message = "图片类型不能为空")
    @Pattern(regexp = "(image/jpeg|image/png|image/gif)", message = "非法类型")
    private String contentType;

}
