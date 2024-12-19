package com.anynote.note;

import com.anynote.common.security.annotation.EnableAnyNoteFeignClients;
import com.anynote.common.security.annotation.EnableCustomConfig;
import com.anynote.common.swagger.annotation.EnableCustomSwagger2;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AnyNote Note模块启动类
 * @author 称霸幼儿园
 */
@EnableCustomSwagger2
@EnableAnyNoteFeignClients
@EnableCustomConfig
@SpringBootApplication
@MapperScan({"com.anynote.note.mapper", "com.anynote.common.datascope.mapper"})
@Slf4j
public class AnyNoteNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnyNoteNoteApplication.class, args);
        System.out.println("笔记模块启动");
        log.info("笔记模块启动");
    }
}
