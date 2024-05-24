package com.anynote.ai.model.bo;

import com.anynote.ai.enums.GreenLabel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GreenRes {

    @Data
    @Builder
    public static class Result {
        private GreenLabel greenLabel;

        private String riskWord;

        private Float confidence;
    }

    List<Result> results;

    private String content;
}
