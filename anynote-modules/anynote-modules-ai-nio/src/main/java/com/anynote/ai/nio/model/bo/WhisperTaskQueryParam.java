package com.anynote.ai.nio.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhisperTaskQueryParam {

    /**
     * 任务id
     */
    private Long whisperTaskId;

    /**
     * 认证token
     */
    private String accessToken;
}
