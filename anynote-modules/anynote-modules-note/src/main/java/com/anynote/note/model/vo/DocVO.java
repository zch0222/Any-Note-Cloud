package com.anynote.note.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author 称霸幼儿园
 */
@Data
public class DocVO {


    /**
     * 知识库ID
     */
    private Long id;

    /**
     * 知识库名称
     */
    private String docName;

    private String englishDocName;

    /**
     * 创建者ID
     */
    private Long createBy;

    private String creatorNickname;

    private String creatorUsername;

    private Long knowledgeBaseId;

    private Date createTime;

    private Date updateTime;

    private String url;

    private String hash;

    private Integer permission;

}
