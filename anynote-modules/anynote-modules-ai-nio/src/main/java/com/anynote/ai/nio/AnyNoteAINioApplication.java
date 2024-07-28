package com.anynote.ai.nio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableAnyNoteFeignClients
//@EnableCustomConfig
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.anynote"})
@MapperScan({"com.anynote.common.datascope.mapper", "com.anynote.ai.nio.mapper"})
//@EnableScheduling
public class AnyNoteAINioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnyNoteAINioApplication.class, args);
        System.out.println("AI NIO模块，启动！");
    }
}
