package com.anynote.ai.model.bo;

import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatConversationCreateParam extends ChatConversationQueryParam {

    private String title;

    @Builder(builderMethodName = "ChatConversationCreateParamBuilder")
    public ChatConversationCreateParam(String title, Long docId, Integer type) {
        this.title = title;
        this.setDocId(docId);
        this.setType(type);
    }
}
