package com.anynote.common.green.factory;


import com.anynote.common.green.enums.GreenType;
import com.anynote.common.green.mapper.AliGreenLogMapper;
import com.anynote.common.green.model.bo.AliGreenConfig;
import com.anynote.common.green.plugin.GreenPlugin;
import com.anynote.common.green.plugin.impl.AliGreenPlugin;
import com.anynote.common.redis.service.RedisService;
import com.anynote.core.enums.ConfigEnum;
import com.anynote.core.exception.BusinessException;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GreenPluginFactory {

    @Resource
    private RedisService redisService;

    @Resource
    private AliGreenLogMapper aliGreenLogMapper;

    public GreenPlugin greenPlugin() {
        Gson gson = new Gson();
        GreenType type = GreenType.valueOf(redisService.getConfig(ConfigEnum.GREEN_TYPE).getValue());
        if (GreenType.ALI_GREEN.equals(type)) {
            AliGreenConfig aliGreenConfig = gson.fromJson(redisService.getConfig(ConfigEnum.ALI_GREEN_CONFIG).getValue(),
                    AliGreenConfig.class);
            return new AliGreenPlugin(aliGreenConfig, aliGreenLogMapper);
        }
        throw new BusinessException("创建内容合规Plugin失败");
    }
}
