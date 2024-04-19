package com.anynote.ai.plugin.impl;

import com.anynote.ai.api.model.bo.DeepLTextReq;
import com.anynote.ai.api.model.bo.DeepLTextRes;
import com.anynote.ai.api.model.bo.Translation;
import com.anynote.ai.model.bo.DeepLConfig;
import com.anynote.ai.plugin.TranslatePlugin;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.SpringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeepLTranslatePlugin implements TranslatePlugin {


    private final RestTemplate restTemplate;;

    private final DeepLConfig deepLConfig;

    public DeepLTranslatePlugin(DeepLConfig deepLConfig, RestTemplate restTemplate) {
        this.deepLConfig = deepLConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Translation> translate(List<String> text, String targetLang) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "DeepL-Auth-Key " + deepLConfig.getToken());

        HttpEntity<DeepLTextReq> httpEntity = new HttpEntity<>(DeepLTextReq.builder()
                .target_lang(targetLang)
                .text(text)
                .build(), headers);

        ResponseEntity<DeepLTextRes> response = restTemplate.exchange(deepLConfig.getTextTranslateEndPoint(),
                HttpMethod.POST, httpEntity, DeepLTextRes.class);
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new BusinessException("DeepL翻译服务调用失败");
        }
        List<Translation> translations = new ArrayList<>();
        for (DeepLTextRes.DeepLTranslation translation : Objects.requireNonNull(response.getBody()).getTranslations()) {
            translations.add(Translation.builder()
                            .detectedSourceLanguage(translation.getDetected_source_language())
                            .text(translation.getText()).build());
        }
        return translations;
    }
}
