package com.anynote.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.bo.RagFileQueryRes;
import com.anynote.ai.service.FileRagService;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.ServletUtils;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CountDownLatch;

/**
 * @author 称霸幼儿园
 */
@Service
@Slf4j
public class FileRagServiceImpl implements FileRagService {

//    @Resource
//    private RedisService redisService;

    @Resource
    private ConfigService configService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private WebClient webClient;

    private final String CONTENT_TYPE = "text/plain;charset=UTF-8";

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


    @Override
    public void queryFile(RagFileQueryReq ragFileQueryReq) throws IOException {
        String aiServerAddress = configService.getAIServerAddress();
//        HttpEntity<RagFileQueryReq> httpEntity = new HttpEntity<>(ragFileQueryReq);
        ParameterizedTypeReference<ResData<RagFileQueryRes>> responseType =
                new ParameterizedTypeReference<ResData<RagFileQueryRes>>() {};

        Flux<RagFileQueryRes> resFlux = webClient.post()
                .uri(aiServerAddress + "/api/rag/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(JSON.toJSONString(ragFileQueryReq)))
                .exchangeToFlux(res -> {
                    if (res.statusCode().isError()) {
                        throw new BusinessException("Rag查询失败");
                    }
                    else {
                        return res.bodyToFlux(RagFileQueryRes.class);
                    }
                });
        HttpServletResponse response = ServletUtils.getResponse();
        response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        Writer writer = response.getWriter();

        CountDownLatch latch = new CountDownLatch(1);

        resFlux.subscribe(
                value -> {
                    String resJson = JSON.toJSONString(value);
                    log.info(resJson);
                    try {
                        writer.write(String.format("id: %s\nevent: message\ndata: %s\n\n", System.currentTimeMillis(), resJson));
                        writer.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    error.printStackTrace();
                    latch.countDown();
                },
                latch::countDown
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


//        ResponseEntity<ResData<RagFileQueryRes>> response = restTemplate.exchange(aiServerAddress + "/api/rag/query",
//                HttpMethod.POST, httpEntity, responseType);
//        if (!HttpStatus.OK.equals(response.getStatusCode())) {
//            throw new BusinessException("调用AI服务器失败");
//        }
//        return RemoteResDataUtil.getResData(response.getBody(), "查询失败");
    }
}
