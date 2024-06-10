package com.anynote.notify.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 称霸幼儿园
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVO {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    private Integer level;

    private Date createTime;

    private Date updateTime;

    private String knowledgeName;

    private Long knowledgeBaseId;
}
