package com.anynote.ai.fastapi.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Data
@RefreshScope
@ConfigurationProperties(prefix = "anynote.ai-fastapi")
public class AIFastApiProperties {

    /**
     * FastApi 服务地址
     */
    private String address;
}
