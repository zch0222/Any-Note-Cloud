package com.anynote.ai.api.model.bo;

import com.anynote.ai.api.model.vo.WhisperSubmitVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhisperTaskCreatedMQParam {

    private Long userId;

    private Long whisperTaskId;

    private WhisperSubmitVO whisperSubmitVO;
}
