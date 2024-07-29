package com.anynote.ai.fastapi.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 客户端
 * @author 称霸幼儿园
 */
@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient() {
        return WebClient.create();
    }
}
