package com.anynote.common.redis.service;

import com.anynote.core.enums.ConfigEnum;
import com.anynote.system.api.model.po.SysConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ConfigService {

    @Resource
    private RedisService redisService;

    public String getAIServerAddress() {
        SysConfig sysConfig = (SysConfig) redisService.getCacheObject(ConfigEnum.AI_SERVER_ADDRESS.name());
        return sysConfig.getValue();
    }

    public Integer getRagMaxDayCount() {
        SysConfig sysConfig = redisService.getConfig(ConfigEnum.RAG_MAX_DAY_COUNT);
        return Integer.valueOf(sysConfig.getValue());
    }

}
