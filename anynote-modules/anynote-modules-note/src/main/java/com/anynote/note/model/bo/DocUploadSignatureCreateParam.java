package com.anynote.note.model.bo;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocUploadSignatureCreateParam extends DocQueryParam {

    private String contentType;

    @Builder(builderMethodName = "DocUploadSignatureCreateParamBuilder")
    public DocUploadSignatureCreateParam(String docName, String contentType, Long knowledgeBaseId) {
        this.contentType = contentType;
        this.setDocName(docName);
        this.setKnowledgeBaseId(knowledgeBaseId);
    }

}
