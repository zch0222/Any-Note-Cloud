package com.anynote.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *
 * @author 称霸幼儿园
 */
@EnableAdminServer
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AnyNoteAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnyNoteAdminApplication.class, args);
        System.out.println("Anynote Admin 启动");
    }

}
