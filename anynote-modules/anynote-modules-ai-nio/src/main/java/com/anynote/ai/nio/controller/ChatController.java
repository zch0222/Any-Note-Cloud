package com.anynote.ai.nio.controller;

import com.anynote.ai.nio.model.vo.ChatConversationVO;
import com.anynote.ai.nio.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RestController
@RequestMapping("chat")
@Slf4j
public class ChatController {

    @Resource
    private ChatService chatService;


    @GetMapping(path = "conversations/{id}")
    public Mono<ChatConversationVO> getChatConversationById(@PathVariable("id") Long id,
                                                            @RequestHeader("accessToken") String accessToken) {
        return Mono.just(null);
//        return Mono.just(ResUtil.success("5555"));
//        log.info(accessToken);
//        return chatService.getConversationById(ChatConversationQueryParam.builder()
//                .conversationId(id).accessToken(accessToken).build());

    }

}
