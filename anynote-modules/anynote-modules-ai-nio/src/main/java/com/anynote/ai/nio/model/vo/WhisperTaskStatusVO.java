package com.anynote.ai.nio.model.vo;

import lombok.Data;

@Data
public class WhisperTaskStatusVO {

    @Data
    public static class WhisperTaskResult {
        /**
         * 文本
         */
        private String text;

        /**
         * 字幕文件链接
         */
        private String srt;

        /**
         * txt文件链接
         */
        private String txt;
    }

    /**
     * 状态
     */
    private String status;

    /**
     * 任务id
     */
    private String taskId;

    private WhisperTaskResult result;
}
