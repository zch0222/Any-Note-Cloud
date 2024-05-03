package com.anynote.ai.service.impl;

import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.datascope.annotation.RequiresChatConversationPermissions;
import com.anynote.ai.enums.ChatConversationPermissions;
import com.anynote.ai.model.bo.ChatConversationQueryParam;
import com.anynote.ai.model.vo.ChatConversationInfoVO;
import com.anynote.ai.model.vo.ChatConversationVO;
import com.anynote.ai.service.ChatConversationService;
import com.anynote.ai.service.ChatMessageService;
import com.anynote.ai.service.ChatService;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.po.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {


    @Resource
    private ChatConversationService chatConversationService;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private TokenUtil tokenUtil;

    @Override
    public Long insertChatConversation(ChatConversation chatConversation) {
        int count = chatConversationService.getBaseMapper().insert(chatConversation);
        if (1 == count) {
            return chatConversation.getId();
        }
        throw new BusinessException("创建chat conversation失败");
    }

    @Override
    public Long insertChatMessage(ChatMessage chatMessage) {
        int count = chatMessageService.getBaseMapper().insert(chatMessage);
        if (1 == count) {
            return chatMessage.getId();
        }
        throw new BusinessException("创建chat message失败");
    }

    @Override
    public List<ChatMessage> selectChatMessageList(Long conversationId) {
        LambdaQueryWrapper<ChatMessage> chatMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatMessageLambdaQueryWrapper
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getOrderIndex);
        return chatMessageService.getBaseMapper().selectList(chatMessageLambdaQueryWrapper);
    }

    @Override
    public ChatConversationPermissions getConversationPermissions(Long conversationId) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
            return ChatConversationPermissions.MANAGE;
        }
        LambdaQueryWrapper<ChatConversation> chatConversationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatConversationLambdaQueryWrapper
                .eq(ChatConversation::getId, conversationId)
                .select(ChatConversation::getId, ChatConversation::getPermissions, ChatConversation::getCreateBy);

        ChatConversation conversation = chatConversationService.getBaseMapper()
                .selectOne(chatConversationLambdaQueryWrapper);

        if (StringUtils.isNull(conversation)) {
            throw new BusinessException("对话不存在");
        }

        int permission = 0;
        if (loginUser.getUserId().equals(conversation.getCreateBy())) {
            permission = Integer.parseInt(conversation.getPermissions().substring(0, 1));
        }

        return ChatConversationPermissions.parse(permission);
    }

    @RequiresChatConversationPermissions(ChatConversationPermissions.READ)
    @Override
    public ChatConversationVO getConversationById(ChatConversationQueryParam queryParam) {
        ChatConversation conversation = this.chatConversationService.getBaseMapper()
                .selectById(queryParam.getConversationId());
        List<ChatMessage> messages = this.selectChatMessageList(queryParam.getConversationId());
        return ChatConversationVO.builder()
                .conversation(ChatConversationInfoVO.builder()
                        .id(conversation.getId())
                        .title(conversation.getTitle())
                        .type(conversation.getType())
                        .docId(conversation.getDocId())
                        .permission(this.getConversationPermissions(queryParam.getConversationId()).getValue())
                        .createBy(conversation.getCreateBy())
                        .createTime(conversation.getCreateTime())
                        .updateBy(conversation.getUpdateBy())
                        .updateTime(conversation.getUpdateTime())
                        .build())
                .messages(messages)
                .build();
    }

    private ChatConversationInfoVO buildChatConversationInfoVO(ChatConversation conversation) {
        return ChatConversationInfoVO.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .type(conversation.getType())
                .docId(conversation.getDocId())
                .permission(this.getConversationPermissions(conversation.getId()).getValue())
                .createBy(conversation.getCreateBy())
                .createTime(conversation.getCreateTime())
                .updateBy(conversation.getUpdateBy())
                .updateTime(conversation.getUpdateTime())
                .build();
    }

    @Override
    public PageBean<ChatConversationInfoVO> getConversationList(ChatConversationQueryParam queryParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        LambdaQueryWrapper<ChatConversation> chatConversationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatConversationLambdaQueryWrapper
                .eq(ChatConversation::getDocId, queryParam.getDocId())
                .eq(ChatConversation::getType, queryParam.getType())
                .eq(ChatConversation::getCreateBy, loginUser.getUserId());
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "update_time DESC");
        List<ChatConversation> chatConversations =
                this.chatConversationService.getBaseMapper().selectList(chatConversationLambdaQueryWrapper);
        PageInfo<ChatConversation> pageInfo = new PageInfo<>(chatConversations);

        List<ChatConversationInfoVO> chatConversationInfoVOS = new ArrayList<>(chatConversations.size());
        for (ChatConversation conversation : chatConversations) {
            chatConversationInfoVOS.add(this.buildChatConversationInfoVO(conversation));
        }
        return PageBean.<ChatConversationInfoVO>builder()
                .current(queryParam.getPage())
                .pages(pageInfo.getPages())
                .rows(chatConversationInfoVOS)
                .total(pageInfo.getTotal())
                .build();
    }
}
