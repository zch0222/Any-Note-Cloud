package com.anynote.note.controller;

import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.model.bo.VideoQueryParam;
import com.anynote.note.model.dto.VideoListDTO;
import com.anynote.note.model.vo.VideoListVO;
import com.anynote.note.service.VideoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Video Controller
 * @author 称霸幼儿园
 */
@RestController
@RequestMapping("videos")
public class VideoController {

    @Resource
    private VideoService videoService;


    @GetMapping("")
    public ResData<VideoListVO> getVideoList(@Valid VideoListDTO videoListDTO) {
        return ResData.success(videoService.getVideoList(VideoQueryParam.builder()
                .folderId(videoListDTO.getFolderId())
                .knowledgeBaseId(videoListDTO.getKnowledgeBaseId())
                .build()));
    }

    @PostMapping("upload")
    public ResData<HuaweiOBSTemporarySignature> videoUploadTempLink() {
        return null;
    }
}
