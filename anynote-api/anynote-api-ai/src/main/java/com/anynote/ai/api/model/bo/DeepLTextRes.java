package com.anynote.ai.api.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class DeepLTextRes {

    @Data
    public static class DeepLTranslation {
        private String detected_source_language;

        private String text;
    }

    private List<DeepLTranslation> translations;
}

