package com.anynote.ai.nio.service.impl;

import com.anynote.ai.api.model.po.WhisperTaskLog;
import com.anynote.ai.nio.mapper.WhisperTaskLogMapper;
import com.anynote.ai.nio.service.WhisperTaskLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WhisperTaskLogServiceImpl extends ServiceImpl<WhisperTaskLogMapper, WhisperTaskLog>
        implements WhisperTaskLogService {
}
