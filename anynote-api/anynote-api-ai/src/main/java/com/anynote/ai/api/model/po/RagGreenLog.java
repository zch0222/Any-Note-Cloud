package com.anynote.ai.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("a_rag_green_log")
@NoArgsConstructor
@AllArgsConstructor
public class RagGreenLog extends BaseEntity {

    private Long id;

    private Long conversationId;

    private Long messageId;

    private Long ragLogId;

    private Integer type;

    private String content;

    private String riskWord;

    private String label;

    private String chineseMeaning;

    private Long userId;

    /**
     * 删除标记 0.正常 1.删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer deleted;

    @Builder
    public RagGreenLog(Long id, Long conversationId, Long messageId, Long ragLogId, Integer type, String content,
                       String riskWord, String label, String chineseMeaning, Long userId, Integer deleted, Long createBy,
                       Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.ragLogId = ragLogId;
        this.type = type;
        this.content = content;
        this.riskWord = riskWord;
        this.label = label;
        this.chineseMeaning = chineseMeaning;
        this.userId = userId;
        this.deleted = deleted;
    }

}
