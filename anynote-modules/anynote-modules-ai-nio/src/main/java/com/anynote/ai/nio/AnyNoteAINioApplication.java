package com.anynote.ai.nio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableAnyNoteFeignClients
//@EnableCustomConfig
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.anynote"})
//@EnableScheduling
public class AnyNoteAINioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnyNoteAINioApplication.class, args);
        System.out.println("AI NIO模块，启动！");
    }
}
