package com.anynote.ai.api.factory;

import com.anynote.ai.api.RemoteTranslateService;
import com.anynote.ai.api.model.bo.Translation;
import com.anynote.ai.api.model.dto.TranslateTextDTO;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteTranslateFallbackFactory implements FallbackFactory<RemoteTranslateService> {


    @Override
    public RemoteTranslateService create(Throwable cause) {
        return new RemoteTranslateService() {
            @Override
            public ResData<List<Translation>> translateText(TranslateTextDTO translateTextDTO, String fromSource) {
                throw new BusinessException("翻译文字失败");
            }
        };
    }
}
