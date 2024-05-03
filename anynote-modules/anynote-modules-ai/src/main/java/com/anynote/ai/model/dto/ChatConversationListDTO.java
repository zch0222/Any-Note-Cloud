package com.anynote.ai.model.dto;

import lombok.Data;
import org.apache.rocketmq.common.message.Message;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ChatConversationListDTO {

    private Long id;

    private Long docId;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码错误")
    private Integer page;


    @NotNull(message = "页面容量错误")
    @Max(value = 100, message = "页面容量错误")
    @Min(value = 1, message = "页面容量错误")
    private Integer pageSize;
}
