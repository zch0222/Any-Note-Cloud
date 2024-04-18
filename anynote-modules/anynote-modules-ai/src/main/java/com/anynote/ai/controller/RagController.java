package com.anynote.ai.controller;

import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.bo.RagFileQueryRes;
import com.anynote.ai.service.RagService;
import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("rag")
public class RagController {

    @Resource
    private RagService ragService;

    @InnerAuth
    @PostMapping("index")
    public ResData<RagFileIndexRes> indexFile(@Validated @RequestBody RagFileIndexReq ragFileIndexReq) {
        return ResUtil.success(ragService.indexFile(ragFileIndexReq));
    }

    @InnerAuth
    @PostMapping("query")
    public ResData<RagFileQueryRes> queryFile(@Validated @RequestBody RagFileQueryReq ragFileQueryReq) {
        return ResUtil.success(ragService.queryFile(ragFileQueryReq));
    }

}
