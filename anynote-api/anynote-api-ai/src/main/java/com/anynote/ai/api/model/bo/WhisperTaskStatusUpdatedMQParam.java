package com.anynote.ai.api.model.bo;

import com.anynote.ai.api.model.vo.WhisperTaskStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhisperTaskStatusUpdatedMQParam {

    private WhisperTaskStatusVO whisperTaskStatusVO;

    private Long whisperTaskId;

    private Long userId;

    private Date updateTime;

    private Date createTime;
}
