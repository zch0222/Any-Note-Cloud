package com.anynote.ai.api;

import com.anynote.ai.api.factory.RemoteRagFallbackFactory;
import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.bo.RagFileQueryRes;
import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author 称霸幼儿园
 */
@FeignClient(contextId = "remoteRagService",
            value = ServiceNameConstants.AI_SERVICE, fallbackFactory = RemoteRagFallbackFactory.class)
public interface RemoteRagService {

//    @PostMapping("/rag/index")
//    public ResData<RagFileIndexRes> indexFile(@RequestBody RagFileIndexReq ragFileIndexReq);

    @PostMapping("/rag/index")
    public ResData<RagFileIndexRes> indexFile(@RequestBody RagFileIndexReq ragFileIndexReq,
                                              @RequestHeader("from-source") String fromSource);

    @PostMapping("/rag/query")
    public void queryFile(@RequestBody RagFileQueryReq ragFileQueryReq);

}
