package com.anynote.notify.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ配置类
 * @author 称霸幼儿园
 */
@Slf4j
@Configuration
public class RocketMQClientConfig {

//    @Value("${rocketmq.name-server}")
//    private String nameServer;
//
//    @Bean(name = "rocketMQClientServiceProvider")
//    public ClientServiceProvider rocketMQClientServiceProvider() {
//        return ClientServiceProvider.loadService();
//    }
//
//    @Bean(name = "rocketMQClientConfiguration")
//    public ClientConfiguration rocketMQClientConfiguration() {
//        log.info("nameserver:{}",nameServer);
//        return ClientConfiguration.newBuilder()
//                .setEndpoints(nameServer)
//                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
//                // client configuration to solve the problem please if SSL is not essential.
//                // .enableSsl(false)
//                .build();
//    }

}
