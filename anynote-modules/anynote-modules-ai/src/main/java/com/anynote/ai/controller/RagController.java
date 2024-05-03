package com.anynote.ai.controller;

import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.dto.DocQueryDTO;
import com.anynote.ai.model.bo.DocRagQueryParam;
import com.anynote.ai.service.FileRagService;
import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("rag")
public class RagController {

    @Resource
    private FileRagService fileRagService;

    @InnerAuth
    @PostMapping("index")
    public ResData<RagFileIndexRes> indexFile(@Validated @RequestBody RagFileIndexReq ragFileIndexReq) {
        return ResUtil.success(fileRagService.indexFile(ragFileIndexReq));
    }

    @InnerAuth
    @PostMapping("query")
    public void queryFile(@Validated @RequestBody RagFileQueryReq ragFileQueryReq) throws IOException {
        fileRagService.queryFile(ragFileQueryReq);
    }

    @PostMapping("query/docs")
    public void queryDoc(@Validated @RequestBody DocQueryDTO docQueryDTO) throws IOException {
        fileRagService.queryDoc(DocRagQueryParam.DocRagQueryParamBuilder()
                        .conversionId(docQueryDTO.getConversationId())
                        .docId(docQueryDTO.getDocId())
                        .prompt(docQueryDTO.getPrompt())
                .build());
    }

}
