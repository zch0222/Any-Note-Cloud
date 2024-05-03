package com.anynote.ai.service.impl;

import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.mapper.ChatMessageMapper;
import com.anynote.ai.service.ChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
        implements ChatMessageService {

}
