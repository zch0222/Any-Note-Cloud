package com.anynote.notify.api.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticePublishParam {

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    private Integer level;

    private Date createTime;

    private Long createBy;

    private Date updateTime;

    private Long updateBy;

    private Long knowledgeBaseId;

    private List<Long> userIdList;

}
