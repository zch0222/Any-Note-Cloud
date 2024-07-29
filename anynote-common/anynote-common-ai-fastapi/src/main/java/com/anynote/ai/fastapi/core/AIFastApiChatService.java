package com.anynote.ai.fastapi.core;

import com.anynote.ai.fastapi.model.dto.FastApiChatCompletionsDTO;
import com.anynote.ai.fastapi.model.vo.FastApiChatCompletionsVO;
import com.anynote.ai.fastapi.properties.AIFastApiProperties;
import com.anynote.core.utils.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

@Component
public class AIFastApiChatService {

    @Resource
    private WebClient webClient;

    @Resource
    private AIFastApiProperties aiFastApiProperties;


    public Flux<FastApiChatCompletionsVO> chatCompletions(FastApiChatCompletionsDTO chatCompletionsDTO) {
        return webClient.post()
                .uri(StringUtils.format("{}/v1/chat/completions", aiFastApiProperties.getAddress()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(chatCompletionsDTO))
                .retrieve()
                .bodyToFlux(FastApiChatCompletionsVO.class);
    }

}