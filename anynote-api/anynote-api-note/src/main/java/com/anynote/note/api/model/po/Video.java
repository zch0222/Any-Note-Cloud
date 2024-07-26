package com.anynote.note.api.model.po;

import com.anynote.core.web.model.bo.PermissionEntity;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Map;

/**
 * 视频PO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("n_video")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video extends PermissionEntity {

    /**
     * 视频id
     */
    private Long id;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 知识库id
     */
    private Long knowledgeBaseId;

    /**
     * 视频文件夹id
     */
    private Long videoFolderId;

    /**
     * 视频类型
     * 0. mp4
     */
    private Integer type;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer deleted;

    @Builder
    public Video(Long id, String fileId, String videoName, Long knowledgeBaseId, Long videoFolderId, Integer type, Integer deleted, String permissions, Integer dataScope,
                 Long createBy, Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(permissions, dataScope, createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.fileId = fileId;
        this.videoName = videoName;
        this.knowledgeBaseId = knowledgeBaseId;
        this.videoFolderId = videoFolderId;
        this.type = type;
        this.deleted = deleted;
    }
}
