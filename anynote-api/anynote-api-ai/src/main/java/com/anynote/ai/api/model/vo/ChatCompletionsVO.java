package com.anynote.ai.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionsVO {

    private String status;

    private String message;

    private Long conversationId;
}
