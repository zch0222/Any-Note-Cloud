package com.anynote.ai.api.model.po;

import com.anynote.core.web.model.bo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("a_chat_message")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseEntity {

    /**
     * Message ID.
     */
    private Long id;

    /**
     * Conversation ID.
     */
    private Long conversationId;

    /**
     * Order index.
     */
    private Integer orderIndex;

    /**
     * Message content.
     */
    private String content;

    /**
     * Role: 0 for user, 1 for bot.
     */
    private int role;

    /**
     * Type: 0 for document rag.
     */
    private int type;

    /**
     * Document ID.
     */
    private Long docId;

    /**
     * Deletion flag: 0 for normal, 1 for deleted.
     */
    @TableLogic
    @TableField("is_delete")
    private int deleted;

    @Builder
    public ChatMessage(Long id, Long conversationId, Integer orderIndex, String content, int role, int type,
                       Long docId, int deleted, Long createBy,
                       Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.conversationId = conversationId;
        this.orderIndex = orderIndex;
        this.content = content;
        this.role = role;
        this.type = type;
        this.docId = docId;
        this.deleted = deleted;

    }

}
