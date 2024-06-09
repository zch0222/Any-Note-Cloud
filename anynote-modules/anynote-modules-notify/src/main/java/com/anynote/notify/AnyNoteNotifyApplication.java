package com.anynote.notify;


import com.anynote.common.security.handler.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.anynote", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                GlobalExceptionHandler.class
        })
})
@EnableFeignClients(basePackages = {"com.anynote"})
public class AnyNoteNotifyApplication {


    public static void main(String[] args) {
        SpringApplication.run(AnyNoteNotifyApplication.class, args);
        System.out.println("消息模块启动成功");
    }
}
