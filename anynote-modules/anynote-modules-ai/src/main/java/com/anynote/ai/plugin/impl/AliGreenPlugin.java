package com.anynote.ai.plugin.impl;

import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.anynote.ai.enums.GreenLabel;
import com.anynote.ai.mapper.AliGreenLogMapper;
import com.anynote.ai.model.bo.AliGreenConfig;
import com.anynote.ai.model.bo.GreenRes;
import com.anynote.ai.model.po.AliGreenLog;
import com.anynote.ai.plugin.GreenPlugin;
import com.anynote.core.exception.BusinessException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class AliGreenPlugin implements GreenPlugin {

    private AliGreenConfig aliGreenConfig;

    private AliGreenLogMapper aliGreenLogMapper;

    public AliGreenPlugin(AliGreenConfig aliGreenConfig, AliGreenLogMapper aliGreenLogMapper) {
        this.aliGreenConfig = aliGreenConfig;
        this.aliGreenLogMapper = aliGreenLogMapper;
    }


    public com.aliyun.green20220302.Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(this.aliGreenConfig.getAlibabaCloudAccessKeyId())
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(this.aliGreenConfig.getAlibabaCloudAccessKeySecret());
        // Endpoint 请参考 https://api.aliyun.com/product/Green
        config.endpoint = this.aliGreenConfig.getEndpoint();
        return new com.aliyun.green20220302.Client(config);
    }

    private GreenLabel parseLabel(String label) {
        if (label.startsWith("nonLabel")) {
            return GreenLabel.NON_LABEL;
        }
        else if (label.startsWith("pornographic")) {
            return GreenLabel.PORNOGRAPHIC;
        }
        else if (label.startsWith("sexual")) {
            return GreenLabel.SEXUAL;
        }
        else if (label.startsWith("political")) {
            return GreenLabel.POLITICAL;
        }
        else if (label.startsWith("violent")) {
            return GreenLabel.VIOLENT;
        }
        else if (label.startsWith("contraband")) {
            return GreenLabel.CONTRABAND;
        }
        else if (label.startsWith("inappropriate")) {
            return GreenLabel.INAPPROPRIATE;
        }
        else if (label.startsWith("pt")) {
            return GreenLabel.PT;
        }
        else if (label.startsWith("religion")) {
            return GreenLabel.RELIGION;
        }
        else if (label.startsWith("customized")) {
            return GreenLabel.CONTRABAND;
        }
        return GreenLabel.NON_LABEL;
    }

    private GreenRes llmModeration(String content, String service) throws Exception {
        Client client = null;
        Gson gson = new Gson();
        Date now = new Date();
        AliGreenLog aliGreenLog = AliGreenLog.builder()
                .service(service)
                .content(content)
                .deleted(0)
                .createBy(0L)
                .createTime(now)
                .updateBy(0L)
                .updateTime(now)
                .build();

        try {
            client = this.createClient();
        } catch (Exception e) {
            log.error("创建阿里云内容安全检测Client失败", e);
            aliGreenLog.setStatus(1);
            aliGreenLogMapper.insert(aliGreenLog);
            throw e;
        }
        Map<String, String> serviceParametersMap = new HashMap<>();
        serviceParametersMap.put("content", content);
        TextModerationPlusRequest textModerationPlusRequest = new com.aliyun.green20220302.models.TextModerationPlusRequest()
                .setService(service)
                .setServiceParameters(gson.toJson(serviceParametersMap));
        RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        TextModerationPlusResponse response = null;

        try {
            // 复制代码运行请自行打印 API 的返回值
            assert client != null;
            response = client.textModerationPlusWithOptions(textModerationPlusRequest, runtime);
        } catch (Exception _error) {
            log.error("阿里云内容安全检测请求失败", _error);
            aliGreenLog.setStatus(1);
            aliGreenLogMapper.insert(aliGreenLog);
            throw _error;
        }

        String resString = gson.toJson(response);

        log.info(resString);
        aliGreenLog.setStatus(0);
        aliGreenLog.setResponse(resString);
        aliGreenLogMapper.insert(aliGreenLog);
        List<GreenRes.Result> resultList = new ArrayList<>();

        for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult result :
                response.getBody().data.getResult()) {
            resultList.add(GreenRes.Result.builder()
                    .greenLabel(parseLabel(result.getLabel()))
                    .riskWord(result.getRiskWords())
                    .confidence(result.getConfidence())
                    .build());
        }
        return GreenRes.builder()
                .results(resultList)
                .content(content)
                .build();
    }


    @Override
    public GreenRes llmQueryModeration(String content) throws Exception {
        return this.llmModeration(content, "llm_query_moderation");
//        Client client = null;
//        Gson gson = new Gson();
//
//        try {
//            client = this.createClient();
//        } catch (Exception e) {
//            log.error("创建阿里云内容安全检测Client失败", e);
//            throw e;
//        }
//        Map<String, String> serviceParametersMap = new HashMap<>();
//        serviceParametersMap.put("content", content);
//        TextModerationPlusRequest textModerationPlusRequest = new com.aliyun.green20220302.models.TextModerationPlusRequest()
//                .setService("llm_query_moderation")
//                .setServiceParameters(gson.toJson(serviceParametersMap));
//        RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//
//        TextModerationPlusResponse response = null;
//
//        try {
//            // 复制代码运行请自行打印 API 的返回值
//            assert client != null;
//            response = client.textModerationPlusWithOptions(textModerationPlusRequest, runtime);
//        } catch (Exception _error) {
//            log.error("阿里云内容安全检测请求失败", _error);
//            throw _error;
//        }
//
//        log.info(new Gson().toJson(response));
//        List<GreenRes> greenResList = new ArrayList<>();
//
//        for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult result :
//                response.getBody().data.getResult()) {
//            greenResList.add(GreenRes.builder()
//                            .greenLabel(parseLabel(result.getLabel()))
//                            .riskWord(result.getRiskWords())
//                            .confidence(result.getConfidence())
//                    .build());
//        }
//        return greenResList;
    }

    @Override
    public GreenRes llmResponseModeration(String content) throws Exception {
        return this.llmModeration(content, "llm_response_moderation");
    }
}
