package com.anynote.ai.model.bo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversationQueryParam {

    private Long conversationId;

    private Long docId;

    private Integer page;

    private Integer pageSize;

    private Integer type;

}
