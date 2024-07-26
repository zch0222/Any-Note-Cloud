package com.anynote.note.model.vo;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.anynote.note.api.model.po.Video;
import com.anynote.note.api.model.po.VideoFolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoListVO {

    private VideoFolder folderInfo;

    /**
     * 文件夹列表
     */
    private List<VideoFolder> videoFolders;

    /**
     * 视频列表
     */
    private List<Video> videos;
}
