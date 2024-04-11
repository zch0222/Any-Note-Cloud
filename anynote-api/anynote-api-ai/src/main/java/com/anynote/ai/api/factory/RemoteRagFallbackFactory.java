package com.anynote.ai.api.factory;

import com.anynote.ai.api.RemoteRagService;
import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author 称霸幼儿园
 */
@Slf4j
@Component
public class RemoteRagFallbackFactory implements FallbackFactory<RemoteRagService> {


    @Override
    public RemoteRagService create(Throwable cause) {
        return new RemoteRagService() {
            @Override
            public ResData<RagFileIndexRes> indexFile(RagFileIndexReq ragFileIndexReq) {
                throw new BusinessException("索引建立失败");
            }
        };
    }
}
