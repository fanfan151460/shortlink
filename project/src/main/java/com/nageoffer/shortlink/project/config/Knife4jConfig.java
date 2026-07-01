package com.nageoffer.shortlink.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI projectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("短链接核心服务 API")
                        .description("project模块：创建短链接、重定向、统计、回收站")
                        .version("1.0")
                        .contact(new Contact().name("project")));
    }
}
