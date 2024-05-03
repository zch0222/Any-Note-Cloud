package com.anynote.ai.service;

import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.dto.DocQueryDTO;
import com.anynote.ai.model.bo.DocRagQueryParam;

import java.io.IOException;


public interface FileRagService {

    public RagFileIndexRes indexFile(RagFileIndexReq ragFileIndexReq);

    public void queryFile(RagFileQueryReq ragFileQueryReq) throws IOException;

    public void queryDoc(DocRagQueryParam docRagQueryParam) throws IOException;

}
