package com.anynote.note.service.impl;

import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.StringUtils;
import com.anynote.note.api.model.po.Video;
import com.anynote.note.api.model.po.VideoFolder;
import com.anynote.note.mapper.VideoMapper;
import com.anynote.note.model.bo.VideoQueryParam;
import com.anynote.note.model.vo.VideoListVO;
import com.anynote.note.service.VideoFolderService;
import com.anynote.note.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Resource
    private VideoFolderService videoFolderService;

    @Override
    public VideoListVO getVideoList(VideoQueryParam queryParam) {
        List<Video> videoList = this.list(new LambdaQueryWrapper<Video>()
                .eq(StringUtils.isNotNull(queryParam.getVideoId()), Video::getId, queryParam.getVideoId())
                .eq(StringUtils.isNotNull(queryParam.getKnowledgeBaseId()), Video::getKnowledgeBaseId, queryParam.getKnowledgeBaseId())
                .eq(StringUtils.isNotNull(queryParam.getFolderId()), Video::getVideoFolderId, queryParam.getFolderId()));
        List<VideoFolder> videoFolderList = videoFolderService.list(new LambdaQueryWrapper<VideoFolder>()
                .eq(StringUtils.isNotNull(queryParam.getFolderId()), VideoFolder::getParentId, queryParam.getFolderId())
                .eq(StringUtils.isNotNull(queryParam.getKnowledgeBaseId()), VideoFolder::getKnowledgeBaseId, queryParam.getKnowledgeBaseId()));
        VideoFolder videoFolder = null;
        if (StringUtils.isNotNull(queryParam.getFolderId()) && 0L != queryParam.getFolderId()) {
            videoFolder = videoFolderService.getById(queryParam.getFolderId());
        }
        return VideoListVO.builder()
                .folderInfo(videoFolder)
                .videos(videoList)
                .videoFolders(videoFolderList)
                .build();
    }
}
