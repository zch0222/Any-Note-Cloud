package com.anynote.ai.service;

import com.anynote.ai.api.model.dto.ConversationCreateDTO;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.model.bo.ChatConversationCreateParam;
import com.anynote.ai.model.bo.ChatConversationUpdateParam;
import com.anynote.ai.model.vo.ChatConversationInfoVO;
import com.anynote.ai.model.vo.ChatConversationVO;
import com.anynote.core.web.model.bo.PageBean;

import java.util.List;

/**
 * @author 称霸幼儿园
 */
public interface ChatService {
    public Long insertChatConversation(ChatConversation chatConversation);

    public Long insertChatMessage(ChatMessage chatMessage);

    public List<ChatMessage> selectChatMessageList(Long conversationId);

    public ChatConversationPermissions getConversationPermissions(Long conversationId);

    public ChatConversationVO getConversationById(ChatConversationQueryParam queryParam);

    public Long createConversation(ChatConversationCreateParam createParam);

    public String updateChatConversation(ChatConversationUpdateParam chatConversationUpdateParam);

    public PageBean<ChatConversationInfoVO> getConversationList(ChatConversationQueryParam queryParam);

}
