package com.anynote.note.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DocListVO {

    /**
     * 知识库ID
     */
    private Long id;

    /**
     * 知识库名称
     */
    private String docName;

    /**
     * 创建者ID
     */
    private Long createBy;

    private Integer indexStatus;

    private String creatorNickname;

    private String creatorUsername;

    private Long knowledgeBaseId;

    private Date createTime;

    private Date updateTime;
}
