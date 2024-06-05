package com.anynote.ai.api.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ConversationCreateDTO {


    @NotEmpty(message = "标题不能为空")
    @Size(max = 10, min = 3, message = "标题不合规")
    private String title;

    @NotNull(message = "对话类型不能为空")
    private Integer type;

    private Long docId;

}
