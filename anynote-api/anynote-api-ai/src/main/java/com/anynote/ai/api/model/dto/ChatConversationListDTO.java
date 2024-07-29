package com.anynote.ai.api.model.dto;

import com.anynote.core.web.model.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatConversationListDTO extends PageDTO {

    private Long docId;
}
