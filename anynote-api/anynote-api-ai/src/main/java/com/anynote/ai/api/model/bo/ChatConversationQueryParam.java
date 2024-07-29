package com.anynote.ai.api.model.bo;


import com.anynote.core.web.model.bo.QueryParam;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversationQueryParam extends QueryParam {

    private Long conversationId;

    private Long docId;

    private Integer page;

    private Integer pageSize;

    private Integer type;

    private String accessToken;

}
