package com.anynote.ai.nio.controller;

import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.api.model.bo.ChatConversationUpdateParam;
import com.anynote.ai.api.model.dto.ChatCompletionsDTO;
import com.anynote.ai.api.model.dto.ChatConversationListDTO;
import com.anynote.ai.api.model.dto.ChatConversationUpdateDTO;
import com.anynote.ai.api.model.vo.ChatCompletionsVO;
import com.anynote.ai.nio.model.vo.ChatConversationInfoVO;
import com.anynote.ai.nio.model.vo.ChatConversationVO;
import com.anynote.ai.nio.service.ChatService;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * AI对话Controller
 */
@RestController
@RequestMapping("chat")
@Slf4j
public class ChatController {

    @Resource
    private ChatService chatService;

    @ApiOperation("对话列表")
    @GetMapping("conversations/list")
    public Mono<ResData<PageBean<ChatConversationInfoVO>>> getChatConversationList(@Valid ChatConversationListDTO chatConversationListDTO) {
        return chatService.getChatConversationList(chatConversationListDTO)
                .flatMap(chatConversationInfoVOPageBean -> Mono.just(ResData.success(chatConversationInfoVOPageBean)));
    }


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

    @ApiOperation("更新对话")
    @PatchMapping("conversations/{id}")
    public Mono<ResData<String>> updateChatConversation(@PathVariable("id") Long id,
                                                        @Valid @RequestBody ChatConversationUpdateDTO chatConversationUpdateDTO) {
        return chatService.updateChatConversation(ChatConversationUpdateParam.ChatConversationUpdateParamBuilder()
                .conversationId(id)
                .title(chatConversationUpdateDTO.getTitle())
                .build())
                .flatMap(res -> Mono.just(ResData.success(res)));
    }



    @ApiOperation("Chat")
    @PostMapping("completions")
    public Flux<ServerSentEvent<ResData<ChatCompletionsVO>>> chatCompletions(@Valid @RequestBody ChatCompletionsDTO chatCompletionsDTO) {
        if (StringUtils.isNotNull(chatCompletionsDTO.getConversationId())) {
            return chatService.authedChatCompletions(chatCompletionsDTO)
                    .flatMap(chatCompletionsVO -> Flux.just(ServerSentEvent
                            .builder(ResData.success(chatCompletionsVO))
                            .event("message")
                            .id(String.valueOf(System.currentTimeMillis()))
                            .build()));
        }
        return chatService.chatCompletions(chatCompletionsDTO)
                .flatMap(chatCompletionsVO -> Flux.just(ServerSentEvent
                        .builder(ResData.success(chatCompletionsVO))
                        .event("message")
                        .id(String.valueOf(System.currentTimeMillis()))
                        .build()));
    }


}
