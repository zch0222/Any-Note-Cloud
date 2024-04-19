package com.anynote.ai.service.impl;

import com.anynote.ai.api.model.bo.Translation;
import com.anynote.ai.api.model.dto.TranslateTextDTO;
import com.anynote.ai.factory.TranslatePluginFactory;
import com.anynote.ai.plugin.TranslatePlugin;
import com.anynote.ai.service.TranslateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TranslateServiceImpl implements TranslateService {

    @Resource
    private TranslatePluginFactory translatePluginFactory;

    @Override
    public List<Translation> translateText(TranslateTextDTO translateTextDTO) {
        return translatePluginFactory.translatePlugin().translate(translateTextDTO.getText(),
                translateTextDTO.getTargetLang());
    }
}
