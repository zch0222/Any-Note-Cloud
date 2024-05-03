package com.anynote.ai.service;

import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.enums.ChatConversationPermissions;
import com.anynote.ai.model.bo.ChatConversationQueryParam;
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

    public PageBean<ChatConversationInfoVO> getConversationList(ChatConversationQueryParam queryParam);

}
