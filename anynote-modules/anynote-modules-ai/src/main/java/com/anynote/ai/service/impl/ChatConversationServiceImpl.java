package com.anynote.ai.service.impl;

import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.mapper.ChatConversationMapper;
import com.anynote.ai.service.ChatConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation>
        implements ChatConversationService {
}
