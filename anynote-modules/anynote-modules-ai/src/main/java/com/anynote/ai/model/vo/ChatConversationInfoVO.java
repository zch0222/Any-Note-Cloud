package com.anynote.ai.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatConversationInfoVO {

    /**
     * 对话id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 对话类型0.文档rag
     */
    private Integer type;

    /**
     * 文档ID
     */
    private Long docId;


    private int permission;

    /** 创建者 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long createBy;

    /** 创建时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Date createTime;

    /** 更新者 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long updateBy;

    /** 更新时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
