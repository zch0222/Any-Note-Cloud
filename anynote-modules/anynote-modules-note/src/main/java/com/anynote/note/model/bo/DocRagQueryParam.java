package com.anynote.note.model.bo;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocRagQueryParam extends DocQueryParam {

    /**
     * prompt
     */
    private String prompt;

    @Builder(builderMethodName = "DocRagQueryParamBuilder")
    public DocRagQueryParam(Long docId, String prompt) {
        this.setDocId(docId);
        this.prompt = prompt;
    }

}
