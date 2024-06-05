package com.anynote.ai.api.model.bo;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocRagQueryParam extends ChatConversationQueryParam {
    private String prompt;

    private Long docId;

    @Builder(builderMethodName = "DocRagQueryParamBuilder")
    public DocRagQueryParam(String prompt, Long docId, Long conversionId) {
        this.docId = docId;
        this.prompt = prompt;
        this.setConversationId(conversionId);
    }

}
