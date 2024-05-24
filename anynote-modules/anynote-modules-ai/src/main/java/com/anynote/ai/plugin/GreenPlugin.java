package com.anynote.ai.plugin;

import com.anynote.ai.enums.GreenLabel;
import com.anynote.ai.model.bo.GreenRes;

import java.util.List;

public interface GreenPlugin {

    /**
     * 大模型输入内容检测
     * @param content
     * @return
     */
    public GreenRes llmQueryModeration(String content) throws Exception;

    public GreenRes llmResponseModeration(String content) throws Exception;

}
