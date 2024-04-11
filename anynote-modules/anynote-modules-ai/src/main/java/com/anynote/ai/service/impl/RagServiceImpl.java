package com.anynote.ai.service.impl;

import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.service.RagService;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.redis.service.RedisService;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author 称霸幼儿园
 */
@Service
public class RagServiceImpl implements RagService {

//    @Resource
//    private RedisService redisService;

    @Resource
    private ConfigService configService;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public RagFileIndexRes indexFile(RagFileIndexReq ragFileIndexReq) {
        String aiServerAddress = configService.getAIServerAddress();
        HttpEntity<RagFileIndexReq> httpEntity = new HttpEntity<>(ragFileIndexReq);
        ParameterizedTypeReference<ResData<RagFileIndexRes>> responseType =
                new ParameterizedTypeReference<ResData<RagFileIndexRes>>() {};
        ResponseEntity<ResData<RagFileIndexRes>> response = restTemplate.exchange(aiServerAddress + "/api/rag/index",
                HttpMethod.POST, httpEntity, responseType);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new BusinessException("调用文件索引服务器失败");
        }
        return RemoteResDataUtil.getResData(response.getBody(), "建立索引失败");
    }
}
