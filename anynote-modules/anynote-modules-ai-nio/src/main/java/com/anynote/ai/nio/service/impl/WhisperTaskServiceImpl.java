package com.anynote.ai.nio.service.impl;

import com.anynote.ai.api.model.po.WhisperTask;
import com.anynote.ai.nio.mapper.WhisperTaskMapper;
import com.anynote.ai.nio.service.WhisperTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WhisperTaskServiceImpl extends ServiceImpl<WhisperTaskMapper, WhisperTask>
        implements WhisperTaskService {
}
