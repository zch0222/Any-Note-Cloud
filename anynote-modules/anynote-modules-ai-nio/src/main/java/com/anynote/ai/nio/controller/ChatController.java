package com.anynote.ai.nio.controller;

import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.nio.model.vo.ChatConversationVO;
import com.anynote.ai.nio.service.ChatService;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * AI对话Controller
 */
@RestController
@RequestMapping("chat")
@Slf4j
public class ChatController {

    @Resource
    private ChatService chatService;


    @ApiOperation("根据id获取对话")
    @GetMapping(path = "conversations/{id}")
    public Mono<ResData<ChatConversationVO>> getChatConversationById(@PathVariable("id") Long id) {

        return chatService.getChatConversationById(ChatConversationQueryParam.builder()
                .conversationId(id)
                .build())
                .flatMap(chatConversation -> Mono.just(ResUtil.success(chatConversation)));
//        return Mono.just(ResUtil.success("5555"));
//        log.info(accessToken);
//        return chatService.getConversationById(ChatConversationQueryParam.builder()
//                .conversationId(id).accessToken(accessToken).build());

    }

}
