package com.anynote.ai.nio.model.vo;

import lombok.Data;

@lombok.Data
public class WhisperVO {

//    public static enum Type {
//        /**
//         * 状态更新
//         */
//        STATUS_UPDATE,
//
//        /**
//         * 心跳
//         */
//        HEARTBEAT
//        ;
//    }

    public static enum Status {
        /**
         * 下载中
         */
        DOWNLOADING,

        /**
         * 运行中
         */
        RUNNING,

        /**
         * 已完成
         */
        FINISHED;
    }

    @lombok.Data
    public static class Data {
        private String text;

        private String srt;

        private String txt;
    }

//    /**
//     * STATUS_UPDATE 状态更新
//     */
//    private Type type;


    private String status;

    private Data data;
}
