package com.anynote.system;

import com.anynote.common.security.annotation.EnableAnyNoteFeignClients;
import com.anynote.common.security.annotation.EnableCustomConfig;
import com.anynote.common.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统模块
 *
 * @author 称霸幼儿园
 */
@EnableCustomSwagger2
@EnableAnyNoteFeignClients
@EnableCustomConfig
@SpringBootApplication
@MapperScan({"com.anynote.system.mapper", "com.anynote.common.datascope.mapper"})
public class AnyNoteSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnyNoteSystemApplication.class, args);
        System.out.println("系统模块启动成功");
    }
}
