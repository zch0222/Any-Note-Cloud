package com.anynote.ai.nio.service.impl;

import com.anynote.ai.api.RemoteChatConversationService;
import com.anynote.ai.api.enums.ChatCompletionsVOStatus;
import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.enums.ChatConversationType;
import com.anynote.ai.api.enums.ChatRole;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.api.model.bo.ChatConversationUpdateParam;
import com.anynote.ai.api.model.dto.ChatCompletionsDTO;
import com.anynote.ai.api.model.dto.ChatConversationListDTO;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.api.model.vo.ChatCompletionsVO;
import com.anynote.ai.fastapi.core.AIFastApiChatService;
import com.anynote.ai.fastapi.model.dto.FastApiChatCompletionsDTO;
import com.anynote.ai.nio.model.vo.ChatConversationInfoVO;
import com.anynote.ai.nio.model.vo.ChatConversationVO;
import com.anynote.ai.nio.service.ChatConversationService;
import com.anynote.ai.nio.service.ChatMessageService;
import com.anynote.ai.nio.service.ChatService;
import com.anynote.common.datascope.annotation.RequiresPermissions;
import com.anynote.common.datascope.constants.PermissionConstants;
import com.anynote.core.constant.Constants;
import com.anynote.core.constant.SpringWebfluxContextConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.SpringUtils;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.model.bo.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

//    @Resource
//    private TokenUtil tokenUtil;

    @Resource
    private ChatConversationService chatConversationService;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private RemoteChatConversationService remoteChatConversationService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private AIFastApiChatService aiFastApiChatService;

    @Resource
    private Gson gson;


    @Override
    public Mono<Long> authConversationPermissions(Long conversationId, ChatConversationPermissions reqPermissions,
                                                     String accessToken) {
        if (StringUtils.isNull(conversationId)) {
            return Mono.error(new AuthException("没有权限执行此操作"));
        }
        return Mono.fromCallable(() -> remoteChatConversationService.getChatConversationPermissions(conversationId,
                "inner", accessToken))
                .publishOn(Schedulers.boundedElastic()).doOnError(throwable -> {
                    log.error("authConversationPermissions远程调用异常", throwable);
                })
                .flatMap(res -> {
                    ChatConversationPermissions permissions = RemoteResDataUtil.getResData(res, "获取权限异常");
                    if (permissions.getValue() > reqPermissions.getValue()) {
                        return Mono.just(conversationId);
                    }
                    return Mono.error(new AuthException("没有权限执行此操作"));
                });
    }

    @RequiresPermissions(value = "a:chatConversation:read", paramIdName = "conversationId")
    @Override
    public Mono<ChatConversationVO> getChatConversationById(ChatConversationQueryParam queryParam) {
        return Mono
                .deferContextual(ctx -> {
                    log.info(StringUtils.format("conversation id = {}", queryParam.getConversationId()));
                    ChatConversation conversation = chatConversationService.getById(queryParam.getConversationId());
                    List<ChatMessage> chatMessageList = chatMessageService.list(new LambdaQueryWrapper<ChatMessage>()
                            .eq(ChatMessage::getConversationId, queryParam.getConversationId())
                            .orderByAsc(ChatMessage::getOrderIndex));
                    Integer permission = ctx.get(PermissionConstants.PERMISSION_CONTEXT_KEY);
                    return Mono.just(ChatConversationVO.builder()
                            .conversation(ChatConversationInfoVO.builder()
                                    .id(conversation.getId())
                                    .title(conversation.getTitle())
                                    .type(conversation.getType())
                                    .docId(conversation.getDocId())
                                    .permission(permission)
                                    .createBy(conversation.getCreateBy())
                                    .createTime(conversation.getCreateTime())
                                    .updateBy(conversation.getUpdateBy())
                                    .updateTime(conversation.getUpdateTime())
                                    .build())
                            .messages(chatMessageList)
                            .build());
                });
    }


    private FastApiChatCompletionsDTO buildFastApiChatCompletionsDTO(ChatConversationVO chatConversationVO, String model) {
        List<FastApiChatCompletionsDTO.Message> messages = chatConversationVO.getMessages().stream()
                .map(chatMessage -> FastApiChatCompletionsDTO.Message.builder()
                        .role(ChatRole.parse(chatMessage.getRole()).name().toLowerCase())
                        .content(chatMessage.getContent())
                        .build())
                .collect(Collectors.toList());
        return FastApiChatCompletionsDTO.builder()
                .messages(messages)
                .model(model)
                .build();
    }

    private ChatMessage saveAskMessage(ChatConversationVO chatConversationVO, String prompt) {
        Date now = new Date();
        List<ChatMessage> chatMessageList = chatConversationVO.getMessages();
        ChatMessage askMessage = ChatMessage.builder()
                .content(prompt)
                .conversationId(chatConversationVO.getConversation().getId())
                .role(ChatRole.USER.getValue())
                .type(ChatConversationType.CHAT.getValue())
                .docId(0L)
                .deleted(0)
                .createBy(chatConversationVO.getConversation().getCreateBy())
                .createTime(now)
                .updateBy(chatConversationVO.getConversation().getCreateBy())
                .updateTime(now)
                .build();
        if (chatMessageList.isEmpty()) {
            askMessage.setOrderIndex(0);
        }
        else {
            askMessage.setOrderIndex(chatMessageList.get(chatMessageList.size()-1).getOrderIndex()+1);
        }
        chatMessageService.getBaseMapper().insert(askMessage);
        chatMessageList.add(askMessage);
        return askMessage;
    }

    private ChatMessage getBaseAnswerChatMessage(Long conversationId,
                                                                  ChatConversationType chatConversationType,
                                                                  Long userId) {
        Date now = new Date();
        return ChatMessage.builder()
                .content("")
                .conversationId(conversationId)
                .role(ChatRole.ASSISTANT.getValue())
                .type(chatConversationType.getValue())
                .docId(0L)
                .deleted(0)
                .createBy(userId)
                .createTime(now)
                .updateBy(userId)
                .updateTime(now)
                .build();
    }

//    private Mono<ChatConversationVO> getChatCompletionsChatConversationVO(Long conversationId) {
//        if (StringUtils.isNotNull(conversationId)) {
//            return SpringUtils.getAopProxy(this).getChatConversationById(ChatConversationQueryParam.builder()
//                            .conversationId(conversationId)
//                            .build())
//                    .publishOn(Schedulers.boundedElastic());
//        }
//        else {
//            return Mono.deferContextual(ctx -> {
//                LoginUser loginUser = ctx.get(SpringWebfluxContextConstants.LOGIN_USER);
//                ChatConversation conversation = ChatConversation.builder()
//                        .title(chatCompletionsDTO.getPrompt().substring(0, Math.min(chatCompletionsDTO.getPrompt().length(), 10)))
//                        .type(ChatConversationType.CHAT.getValue())
//                        .permissions("70000")
//                        .docId(0L)
//                        .deleted(0)
//                        .updateBy(loginUser.getUserId())
//                        .createBy(loginUser.getUserId())
//                        .createTime(now)
//                        .updateTime(now)
//                        .build();
//                chatConversationService.getBaseMapper().insert(conversation);
//                chatCompletionsDTO.setConversationId(conversation.getId());
//                return Mono.just(ChatConversationVO.builder()
//                        .conversation(ChatConversationInfoVO.builder()
//                                .id(conversation.getId())
//                                .title(conversation.getTitle())
//                                .type(conversation.getType())
//                                .docId(conversation.getDocId())
//                                .permission(7)
//                                .build())
//                        .messages(new ArrayList<>())
//                        .build());
//            }).publishOn(Schedulers.boundedElastic());
//        }
//    }


    @Override
    public Flux<ChatCompletionsVO> chatCompletions(ChatCompletionsDTO chatCompletionsDTO) {
        Date now = new Date();
        Mono<ChatConversationVO> chatConversationVOMono = null;
        if (StringUtils.isNotNull(chatCompletionsDTO.getConversationId())) {
            chatConversationVOMono = getChatConversationById(ChatConversationQueryParam.builder()
                    .conversationId(chatCompletionsDTO.getConversationId())
                    .build())
                    .publishOn(Schedulers.boundedElastic());
        }
        else {
            chatConversationVOMono = Mono.deferContextual(ctx -> {
                LoginUser loginUser = ctx.get(SpringWebfluxContextConstants.LOGIN_USER);
                ChatConversation conversation = ChatConversation.builder()
                        .title(chatCompletionsDTO.getPrompt().substring(0, Math.min(chatCompletionsDTO.getPrompt().length(), 10)))
                        .type(ChatConversationType.CHAT.getValue())
                        .permissions("70000")
                        .docId(0L)
                        .deleted(0)
                        .updateBy(loginUser.getUserId())
                        .createBy(loginUser.getUserId())
                        .createTime(now)
                        .updateTime(now)
                        .build();
                chatConversationService.getBaseMapper().insert(conversation);
                chatCompletionsDTO.setConversationId(conversation.getId());
                return Mono.just(ChatConversationVO.builder()
                        .conversation(ChatConversationInfoVO.builder()
                                .id(conversation.getId())
                                .title(conversation.getTitle())
                                .type(conversation.getType())
                                .docId(conversation.getDocId())
                                .permission(7)
                                .build())
                        .messages(new ArrayList<>())
                        .build());
            }).publishOn(Schedulers.boundedElastic());
        }
        AtomicReference<ChatMessage> answerChatMessageAtomicReference = new AtomicReference<>();
        // chat
        return chatConversationVOMono
                // 保存askMessage设置返回消息
                .flatMap(chatConversationVO -> Mono.deferContextual(ctx -> {
                    ChatMessage askChatMessage = this.saveAskMessage(chatConversationVO, chatCompletionsDTO.getPrompt());
                    LoginUser loginUser = ctx.get(SpringWebfluxContextConstants.LOGIN_USER);
                    answerChatMessageAtomicReference
                            .set(ChatMessage.builder()
                                    .content("")
                                    .orderIndex(askChatMessage.getOrderIndex() + 1)
                                    .conversationId(chatCompletionsDTO.getConversationId())
                                    .role(ChatRole.ASSISTANT.getValue())
                                    .type(ChatConversationType.CHAT.getValue())
                                    .docId(0L)
                                    .deleted(0)
                                    .createBy(loginUser.getUserId())
                                    .createTime(now)
                                    .updateBy(loginUser.getUserId())
                                    .updateTime(now)
                                    .build());
                    return Mono.just(chatConversationVO);
                }))
                .flux()
                // 发送Chat
                .flatMap(chatConversationVO -> aiFastApiChatService
                        .chatCompletions(this.buildFastApiChatCompletionsDTO(chatConversationVO,
                                chatCompletionsDTO.getModel())))
                // 返回Chat SSE
                .flatMap(fastApiChatCompletionsVO -> {
                    ChatCompletionsVO chatCompletionsVO = ChatCompletionsVO.builder()
                            .status(ChatCompletionsVOStatus.SUCCESS.name().toLowerCase())
                            .message(fastApiChatCompletionsVO.getChoices().get(0).getDelta().getContent())
                            .conversationId(chatCompletionsDTO.getConversationId())
                            .build();
                    answerChatMessageAtomicReference.updateAndGet(chatMessage -> {
                        chatMessage.setContent(chatMessage.getContent() + chatCompletionsVO.getMessage());
                        return chatMessage;
                    });
                    return Flux.just(chatCompletionsVO);
                })
                // 保存返回的消息
                .doFinally(signalType -> {
                    chatMessageService.getBaseMapper().insert(answerChatMessageAtomicReference.get());
                })
                .doOnError(e -> {
                    log.error("CHAT ERROR", e);
                    answerChatMessageAtomicReference.updateAndGet(chatMessage -> {
                        chatMessage.setContent(chatMessage.getContent() + "\n出现异常请稍后重试");
                        return chatMessage;
                    });
                })
                .onErrorReturn(ChatCompletionsVO.builder()
                        .status(ChatCompletionsVOStatus.FAILED.name().toLowerCase())
                        .message("出现异常请稍后重试")
                        .conversationId(chatCompletionsDTO.getConversationId())
                        .build());
    }

    @Override
    public Mono<PageBean<ChatConversationInfoVO>> getChatConversationList(ChatConversationListDTO chatConversationListDTO) {
        return Mono.deferContextual(ctx -> {
            LoginUser loginUser = ctx.get(SpringWebfluxContextConstants.LOGIN_USER);
            return Mono.fromCallable(() -> {
                log.info(gson.toJson(chatConversationListDTO));
                PageHelper.startPage(chatConversationListDTO.getPage(), chatConversationListDTO.getPageSize(), "update_time DESC");
                List<ChatConversation> chatConversationList = chatConversationService.getBaseMapper()
                        .selectList(new LambdaQueryWrapper<ChatConversation>()
                                .eq(StringUtils.isNotNull(chatConversationListDTO.getDocId()), ChatConversation::getDocId,
                                        chatConversationListDTO.getDocId())
                                .eq(ChatConversation::getCreateBy, loginUser.getUserId()));
                List<ChatConversationInfoVO> chatConversationInfoVOList = chatConversationList.stream()
                        .map(conversation -> ChatConversationInfoVO.builder()
                                .id(conversation.getId())
                                .title(conversation.getTitle())
                                .type(conversation.getType())
                                .docId(conversation.getDocId())
//                            .permission(this.getConversationPermissions(conversation.getId()).getValue())
                                .createBy(conversation.getCreateBy())
                                .createTime(conversation.getCreateTime())
                                .updateBy(conversation.getUpdateBy())
                                .updateTime(conversation.getUpdateTime())
                                .build()).collect(Collectors.toList());
                PageInfo<ChatConversation> pageInfo = new PageInfo<>(chatConversationList);
                return PageBean.<ChatConversationInfoVO>builder()
                        .current(chatConversationListDTO.getPage())
                        .pages(pageInfo.getPages())
                        .rows(chatConversationInfoVOList)
                        .total(pageInfo.getTotal())
                        .build();
            }).publishOn(Schedulers.boundedElastic());
        });
    }

    @RequiresPermissions(value = "a:chatConversation:completions", paramIdName = "conversationId",
            queryParamName = "chatCompletionsDTO", requestType = 1)
    @Override
    public Flux<ChatCompletionsVO> authedChatCompletions(ChatCompletionsDTO chatCompletionsDTO) {
        return chatCompletions(chatCompletionsDTO);
    }

    //    @Override
//    public List<ChatMessage> selectChatMessageList(Long conversationId) {
//        LambdaQueryWrapper<ChatMessage> chatMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        chatMessageLambdaQueryWrapper
//                .eq(ChatMessage::getConversationId, conversationId)
//                .orderByAsc(ChatMessage::getOrderIndex);
//        return chatMessageService.getBaseMapper().selectList(chatMessageLambdaQueryWrapper);
//    }
//
//    @RequiresChatConversationPermissions(ChatConversationPermissions.READ)
//    @Override
//    public Mono<ChatConversationVO> getConversationById(ChatConversationQueryParam queryParam) {
//        return Mono.fromCallable(() -> {
//            log.info("getById:" + queryParam.getConversationId());
//            ChatConversation conversation = this.chatConversationService.getBaseMapper()
//                    .selectById(queryParam.getConversationId());
//            List<ChatMessage> messages = this.selectChatMessageList(queryParam.getConversationId());
//            return ChatConversationVO.builder()
//                    .conversation(ChatConversationInfoVO.builder()
//                            .id(conversation.getId())
//                            .title(conversation.getTitle())
//                            .type(conversation.getType())
//                            .docId(conversation.getDocId())
//                            .permission(0)
//                            .createBy(conversation.getCreateBy())
//                            .createTime(conversation.getCreateTime())
//                            .updateBy(conversation.getUpdateBy())
//                            .updateTime(conversation.getUpdateTime())
//                            .build())
//                    .messages(messages)
//                    .build();
//        }).publishOn(Schedulers.boundedElastic()).log();
////        return Mono
////                .fromCallable(() -> this.chatConversationService.getBaseMapper().selectById(queryParam.getConversationId()))
////                .flatMap(conversation ->
////                        Mono.just(this.selectChatMessageList(queryParam.getConversationId())).map(messages ->
////                                ChatConversationVO.builder()
////                                        .conversation(ChatConversationInfoVO.builder()
////                                                .id(conversation.getId())
////                                                .title(conversation.getTitle())
////                                                .type(conversation.getType())
////                                                .docId(conversation.getDocId())
////                                                .permission(this.getConversationPermissions(queryParam.getConversationId()).getValue())
////                                                .createBy(conversation.getCreateBy())
////                                                .createTime(conversation.getCreateTime())
////                                                .updateBy(conversation.getUpdateBy())
////                                                .updateTime(conversation.getUpdateTime())
////                                                .build())
////                                        .messages(messages)
////                                        .build())).publishOn(Schedulers.boundedElastic());
//
//    }
//
//
//    @Override
//    public ChatConversationPermissions getConversationPermissions(Long conversationId) {
////        LoginUser loginUser = tokenUtil.getLoginUser();
////        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
////            return ChatConversationPermissions.MANAGE;
////        }
////        LambdaQueryWrapper<ChatConversation> chatConversationLambdaQueryWrapper = new LambdaQueryWrapper<>();
////        chatConversationLambdaQueryWrapper
////                .eq(ChatConversation::getId, conversationId)
////                .select(ChatConversation::getId, ChatConversation::getPermissions, ChatConversation::getCreateBy);
////
////        ChatConversation conversation = chatConversationService.getBaseMapper()
////                .selectOne(chatConversationLambdaQueryWrapper);
////
////        if (StringUtils.isNull(conversation)) {
////            throw new BusinessException("对话不存在");
////        }
////
////        int permission = 0;
////        if (loginUser.getUserId().equals(conversation.getCreateBy())) {
////            permission = Integer.parseInt(conversation.getPermissions().substring(0, 1));
////        }
////
////        return ChatConversationPermissions.parse(permission);
//        return null;
//    }

    @RequiresPermissions(value = "a:chatConversation:update", paramIdName = "conversationId",
            queryParamName = "updateParam")
    @Override
    public Mono<String> updateChatConversation(ChatConversationUpdateParam updateParam) {
        return Mono.deferContextual(ctx -> {
            LoginUser loginUser = ctx.get(SpringWebfluxContextConstants.LOGIN_USER);
            return Mono.fromCallable(() -> {
                log.info(gson.toJson(updateParam));
                Date now = new Date();
                ChatConversation conversation = ChatConversation.builder()
                        .id(updateParam.getConversationId())
                        .title(updateParam.getTitle())
                        .updateTime(now)
                        .updateBy(loginUser.getUserId())
                        .build();
                chatConversationService.updateById(conversation);
                return Constants.SUCCESS_RES;
            }).publishOn(Schedulers.boundedElastic());
        });
    }
}
