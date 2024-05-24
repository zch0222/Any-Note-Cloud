package com.anynote.ai.service.impl;

import com.anynote.ai.api.model.po.RagGreenLog;
import com.anynote.ai.mapper.RagGreenLogMapper;
import com.anynote.ai.service.RagGreenLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RagGreenLogServiceImpl extends ServiceImpl<RagGreenLogMapper, RagGreenLog>
        implements RagGreenLogService {
}
