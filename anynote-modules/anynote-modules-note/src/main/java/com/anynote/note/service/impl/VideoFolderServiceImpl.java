package com.anynote.note.service.impl;

import com.anynote.note.api.model.po.VideoFolder;
import com.anynote.note.mapper.VideoFolderMapper;
import com.anynote.note.service.VideoFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VideoFolderServiceImpl extends ServiceImpl<VideoFolderMapper, VideoFolder> implements VideoFolderService {
}
