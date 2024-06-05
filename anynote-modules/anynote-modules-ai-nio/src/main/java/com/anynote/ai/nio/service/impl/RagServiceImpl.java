package com.anynote.ai.nio.service.impl;

import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.DocRagQueryParam;
import com.anynote.ai.nio.model.vo.AIChatVO;
import com.anynote.ai.nio.service.ChatService;
import com.anynote.ai.nio.service.RagService;
import com.anynote.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Slf4j
@Service
public class RagServiceImpl implements RagService {

    @Resource
    private WebClient webClient;

//    @Resource
//    private TokenUtil tokenUtil;

    final private String GREEN_MESSAGE = "对不起，暂时无法回答您的问题";

    @Resource
    private ChatService chatService;


    @Override
    public Flux<AIChatVO> queryDoc(DocRagQueryParam ragQueryParam, String accessToken) {
        return Flux.just(accessToken)
                .flatMap(value -> {
                    return chatService.authConversationPermissions(ragQueryParam.getConversationId(),
                            ChatConversationPermissions.EDIT, value);
                })
                .flatMap(value -> {
                    return Flux.just(AIChatVO.builder().status("finished").build());
                }).log()
                .onErrorResume(throwable -> {
                   log.error("queryDoc: " + ragQueryParam.getDocId(), throwable);
                   return Flux.just(AIChatVO.builder().status("failed").build());
                });
//        return chatService.authConversationPermissions(ragQueryParam.getConversationId(),
//                ChatConversationPermissions.EDIT, accessToken)
//                .flatMap(isAllow -> {
//                    if (isAllow) {
//                        return webClient.post()
//                    }
//                    return null;
//                });
//        return null;
    }
}
