package com.anynote.ai.service;

import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import org.springframework.stereotype.Service;


public interface RagService {

    public RagFileIndexRes indexFile(RagFileIndexReq ragFileIndexReq);

}
