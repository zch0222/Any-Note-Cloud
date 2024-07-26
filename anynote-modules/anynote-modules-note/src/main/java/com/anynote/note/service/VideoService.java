package com.anynote.note.service;

import com.anynote.note.api.model.po.Video;
import com.anynote.note.model.bo.VideoQueryParam;
import com.anynote.note.model.vo.VideoListVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface VideoService extends IService<Video> {

    public VideoListVO getVideoList(VideoQueryParam queryParam);
}
