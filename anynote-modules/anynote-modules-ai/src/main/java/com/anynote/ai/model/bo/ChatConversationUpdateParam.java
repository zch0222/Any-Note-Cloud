package com.anynote.ai.model.bo;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversationUpdateParam extends ChatConversationQueryParam{

    private String title;

    @Builder(builderMethodName = "ChatConversationUpdateParamBuilder")
    public ChatConversationUpdateParam(Long conversationId, String title) {
        this.title = title;
        this.setConversationId(conversationId);
    }

}
