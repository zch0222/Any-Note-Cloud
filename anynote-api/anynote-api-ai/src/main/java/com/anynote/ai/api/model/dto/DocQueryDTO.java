package com.anynote.ai.api.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DocQueryDTO {

    /**
     * 对话id
     */
    private Long conversationId;


    @NotEmpty(message = "问题不能为空")
    private String prompt;

    @NotNull(message = "文档id不能为空")
    private Long docId;
}
