package com.anynote.ai.service;

import com.anynote.ai.api.model.bo.Translation;
import com.anynote.ai.api.model.dto.TranslateTextDTO;

import java.util.List;

public interface TranslateService {

    public List<Translation> translateText(TranslateTextDTO translateTextDTO);

}
