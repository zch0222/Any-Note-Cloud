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
@TableName("n_video_folder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoFolder extends PermissionEntity {

    /**
     * 视频id
     */
    private Long id;

    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 父文件夹id 0表示无父文件夹
     */
    private Long parentId;

    /**
     * 知识库id
     */
    private Long knowledgeBaseId;

    /**
     * 删除标记
     * 0正常
     * 1删除
     */
    @TableLogic
    private Integer deleted;

    @Builder
    public VideoFolder(Long id, String folderName, Long parentId, Long knowledgeBaseId, Integer deleted, String permissions, Integer dataScope,
                       Long createBy, Date createTime, Long updateBy, Date updateTime, String remark, Map<String, Object> params) {
        super(permissions, dataScope, createBy, createTime, updateBy, updateTime, remark, params);
        this.id = id;
        this.folderName = folderName;
        this.parentId = parentId;
        this.knowledgeBaseId = knowledgeBaseId;
        this.deleted = deleted;
    }

}
