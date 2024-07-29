package com.anynote.ai.fastapi.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FastApiChatCompletionsVO {

    @Data
    public static class Choice {

        @Data
        public static class Delta {
            private String content;
        }

        private Long index;

        private Delta delta;
    }

    private String id;

    private String created;

    private String model;

    private List<Choice> choices;

}
