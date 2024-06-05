package com.anynote.ai.nio.model.vo;

import com.anynote.ai.api.model.po.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversationVO {

    private ChatConversationInfoVO conversation;

    private List<ChatMessage> messages;

}
