package com.anynote.ai.api.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionsBO extends ChatConversationQueryParam {

    /**
     * prompt
     */
    private String prompt;

    private String model;

    public ChatCompletionsBO(Long conversationId, String prompt, String model) {
        this.setConversationId(conversationId);
        this.prompt = prompt;
        this.model = model;
    }
}
