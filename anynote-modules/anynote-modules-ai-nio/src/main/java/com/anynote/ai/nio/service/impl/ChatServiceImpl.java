package com.anynote.ai.nio.service.impl;

import com.anynote.ai.api.RemoteChatConversationService;
import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.nio.model.vo.ChatConversationInfoVO;
import com.anynote.ai.nio.model.vo.ChatConversationVO;
import com.anynote.ai.nio.service.ChatConversationService;
import com.anynote.ai.nio.service.ChatMessageService;
import com.anynote.ai.nio.service.ChatService;
import com.anynote.common.datascope.annotation.RequiresPermissions;
import com.anynote.common.datascope.constants.PermissionConstants;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.util.List;

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
                            .eq(ChatMessage::getConversationId, queryParam.getConversationId()));
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
}
