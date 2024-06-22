package com.anynote.ai.nio.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WhisperDTO {

    @NotEmpty(message = "url不能为空")
    private String url;

    @NotEmpty(message = "语言不能为空")
    private String language;
}
