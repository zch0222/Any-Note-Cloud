package com.anynote.ai.service.impl;

import com.anynote.ai.mapper.RagLogMapper;
import com.anynote.ai.api.model.po.RagLog;
import com.anynote.ai.service.RagLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RagLogServiceImpl extends ServiceImpl<RagLogMapper, RagLog>
        implements RagLogService {
}
