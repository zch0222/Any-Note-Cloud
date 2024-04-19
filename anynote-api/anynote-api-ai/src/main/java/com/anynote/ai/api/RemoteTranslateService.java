package com.anynote.ai.api;

import com.anynote.ai.api.factory.RemoteRagFallbackFactory;
import com.anynote.ai.api.factory.RemoteTranslateFallbackFactory;
import com.anynote.ai.api.model.bo.Translation;
import com.anynote.ai.api.model.dto.TranslateTextDTO;
import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @author 称霸幼儿园
 */
@FeignClient(contextId = "remoteTranslateService",
        value = ServiceNameConstants.AI_SERVICE, fallbackFactory = RemoteTranslateFallbackFactory.class)
public interface RemoteTranslateService {


    @PostMapping("/translate")
    public ResData<List<Translation>> translateText(@RequestBody TranslateTextDTO translateTextDTO,
                                                    @RequestHeader("from-source") String fromSource);

}
