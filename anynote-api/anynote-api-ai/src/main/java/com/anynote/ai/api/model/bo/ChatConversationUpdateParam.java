package com.anynote.ai.api.model.bo;

import com.anynote.core.web.model.bo.QueryParam;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ChatConversationUpdateParam extends QueryParam {

    private Long conversationId;

    /**
     * 标题
     */
    private String title;

    @Builder(builderMethodName = "ChatConversationUpdateParamBuilder")
    public ChatConversationUpdateParam(Long conversationId, String title) {
        this.conversationId = conversationId;
        this.title = title;
    }
}
