package com.nageoffer.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication(scanBasePackages = {"com.nageoffer.shortlink.framework",
        "com.nageoffer.shortlink.project"})
@MapperScan("com.nageoffer.shortlink.project.dao.mapper")
@ServletComponentScan(basePackages = "com.nageoffer.shortlink.project.common.web")
public class ShortLinkProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkProjectApplication.class, args);
    }
}
