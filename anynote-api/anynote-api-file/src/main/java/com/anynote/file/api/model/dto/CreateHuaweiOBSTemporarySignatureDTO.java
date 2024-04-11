package com.anynote.file.api.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateHuaweiOBSTemporarySignatureDTO {
    @NotNull(message = "路径不能为空")
    private String path;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotNull(message = "过期时间不能为空")
    @Max(value = 3600, message = "过期时间过长")
    @Min(value = 1, message = "过期时间错误")
    private Long expireSeconds;

    @NotBlank(message = "ContentType不能为空")
    private String contentType;

    /**
     * 文件来源
     */
    @NotNull(message = "文件来源不能为空")
    private Integer source;
}
