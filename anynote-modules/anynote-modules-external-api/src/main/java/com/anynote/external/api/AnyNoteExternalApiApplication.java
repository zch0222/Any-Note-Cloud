package com.anynote.external.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AnyNoteExternalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnyNoteExternalApiApplication.class, args);
        System.out.println("第三方接口模块，启动！");
    }
}
