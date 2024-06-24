package com.anynote.ai.nio.service.impl;

import com.anynote.ai.api.model.po.WhisperTaskText;
import com.anynote.ai.nio.mapper.WhisperTaskTextMapper;
import com.anynote.ai.nio.service.WhisperTaskTextService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WhisperTaskTextServiceImpl extends ServiceImpl<WhisperTaskTextMapper, WhisperTaskText>
        implements WhisperTaskTextService {
}
