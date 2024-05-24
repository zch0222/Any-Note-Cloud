package com.anynote.ai.factory;

import com.anynote.ai.enums.GreenType;
import com.anynote.ai.mapper.AliGreenLogMapper;
import com.anynote.ai.model.bo.AliGreenConfig;
import com.anynote.ai.plugin.GreenPlugin;
import com.anynote.ai.plugin.impl.AliGreenPlugin;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.redis.service.RedisService;
import com.anynote.core.enums.ConfigEnum;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.SpringUtils;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
