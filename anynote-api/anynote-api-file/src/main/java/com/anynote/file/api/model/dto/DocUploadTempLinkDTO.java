package com.anynote.file.api.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DocUploadTempLinkDTO {

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件类型
     */
    @NotBlank(message = "文件类型不能为空")
    private String contentType;

    /**
     * 上传ID
     */
    @NotBlank(message = "上传ID不能为空")
    private String uploadId;
}
