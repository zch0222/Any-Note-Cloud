package com.anynote.ai.plugin;

import com.anynote.ai.api.model.bo.Translation;

import java.util.List;

public interface TranslatePlugin {

    public List<Translation> translate(List<String> text, String targetLang);

}
