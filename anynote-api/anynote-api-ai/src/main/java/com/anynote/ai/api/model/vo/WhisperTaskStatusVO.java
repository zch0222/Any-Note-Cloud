package com.anynote.ai.api.model.vo;

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

    public static enum Status {
        STARTING(0),
        LOADING_MODEL(1),
        DOWNLOADING(2),
        RUNNING(3),
        FINISHED(4),
        ;

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum Type {
        STATUS_UPDATE,
        HEARTBEAT;
    }

    private String type;

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
