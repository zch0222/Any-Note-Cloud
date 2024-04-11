package com.anynote.note.model.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DOC查询参数
 * @author 称霸幼儿园
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DocQueryParam extends KnowledgeBaseQueryParam {

    /**
     * 文档ID
     */
    private Long docId;

    /**
     * 文档名称
     */
    private String docName;

    /**
     * 文档类型 0.PDF
     */
    private Integer docType;


    public void setKnowledgeBaseId(Long id) {
        this.setId(id);
    }

    public Long getKnowledgeBaseId() {
       return this.getId();
    }

    @Builder(builderMethodName = "DocQueryParamBuilder")
    public DocQueryParam(Long docId, Long knowledgeBaseId, String docName, Integer docType, Integer page, Integer pageSize) {
        this.setKnowledgeBaseId(knowledgeBaseId);
        this.docId = docId;
        this.docName = docName;
        this.docType = docType;
        this.setPage(page);
        this.setPageSize(pageSize);
    }
}
