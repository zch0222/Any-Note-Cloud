package com.anynote.ai.service;

import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.bo.DocRagQueryParam;
import com.anynote.ai.model.vo.DocQueryVO;
import com.anynote.core.web.model.bo.ResData;
import reactor.core.publisher.Flux;

import java.io.IOException;


public interface FileRagService {

    public RagFileIndexRes indexFile(RagFileIndexReq ragFileIndexReq);

    public void queryFile(RagFileQueryReq ragFileQueryReq) throws IOException;

    public void queryDoc(DocRagQueryParam docRagQueryParam) throws IOException;

    public Flux<ResData<DocQueryVO>> queryDocV2(DocRagQueryParam docRagQueryParam) throws IOException;

    public Flux<ResData<DocQueryVO>> queryDocFree(DocRagQueryParam docRagQueryParam) throws IOException;
}
