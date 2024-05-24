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
@TableName("a_rag_log")
@NoArgsConstructor
@AllArgsConstructor
public class RagLog extends BaseEntity {

    /**
     * 每条记录的唯一标识符。
     */
    private Long id;

    /**
     * 文件的哈希值，用于确保文件的唯一性。
     */
    private String fileHash;

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 文档的作者。
     */
    private String author;

    /**
     * 文档的类别。
     */
    private String category;

    /**
     * 文档的详细描述。
     */
    private String description;

    /**
     * 与文档处理相关的提示词。
     */
    private String prompt;

    /**
     * 机器人对提示词的回应。
     */
    private String message;

    /**
     * 查询或交互的开始时间。
     */
    private Date startTime;

    /**
     * 查询或交互的结束时间。
     */
    private Date endTime;

    /**
     * 查询的结果（0代表成功，1代表失败 2代表不当内容）。
     */
    private Integer result;

    /**
     * 删除标记 0.正常 1.删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer deleted;


    /**
     * 全参数构造方法。
     *
     * @param id          记录的唯一标识符。
     * @param fileHash    文件的哈希值。
     * @param fileName    文件名。
     * @param author      作者。
     * @param category    类别。
     * @param description 描述。
     * @param prompt      提示词。
     * @param message     机器人回复。
     * @param startTime   查询开始时间。
     * @param endTime     查询结束时间。
     * @param result      查询结果。
     * @param deleted    删除标志。
     * @param createBy    创建者。
     * @param createTime  创建时间。
     * @param updateBy    更新者。
     * @param updateTime  更新时间。
     * @param remark      备注。
     */
    @Builder
    public RagLog(Long id, String fileHash, String fileName, String author, String category, String description,
                   String prompt, String message, Date startTime, Date endTime, Integer result, Integer deleted, Long createBy,
                   Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.fileHash = fileHash;
        this.fileName = fileName;
        this.author = author;
        this.category = category;
        this.description = description;
        this.prompt = prompt;
        this.message = message;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
        this.deleted = deleted;
    }




}
