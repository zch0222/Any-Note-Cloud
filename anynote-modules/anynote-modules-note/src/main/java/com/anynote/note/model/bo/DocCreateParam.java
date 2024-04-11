package com.anynote.note.model.bo;

import lombok.*;

/**
 *
 * @author 称霸幼儿园
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocCreateParam extends DocQueryParam {

    private String uploadId;

    private String hash;

    @Builder(builderMethodName = "DocCreateParamBuilder")
    public DocCreateParam(String uploadId, String hash, Long knowledgeBaseId, String docName) {
        this.uploadId = uploadId;
        this.hash = hash;
        this.setKnowledgeBaseId(knowledgeBaseId);
        this.setDocName(docName);
    }


}
