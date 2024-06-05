package com.anynote.ai.nio.service;

import com.anynote.ai.api.model.bo.DocRagQueryParam;
import com.anynote.ai.nio.model.vo.AIChatVO;
import reactor.core.publisher.Flux;

public interface RagService {


    public Flux<AIChatVO> queryDoc(DocRagQueryParam ragQueryParam, String accessToken);


}
