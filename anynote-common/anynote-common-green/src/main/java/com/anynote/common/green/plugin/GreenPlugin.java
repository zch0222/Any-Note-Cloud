package com.anynote.common.green.plugin;


import com.anynote.common.green.model.bo.GreenRes;

public interface GreenPlugin {

    /**
     * 大模型输入内容检测
     * @param content
     * @return
     */
    public GreenRes llmQueryModeration(String content) throws Exception;

    public GreenRes llmResponseModeration(String content) throws Exception;

}
