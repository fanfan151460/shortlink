package com.nageoffer.shortlink.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI adminOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("短链接管理后台 API")
                        .description("admin模块：用户注册登录、短链接CRUD、分组管理、回收站")
                        .version("1.0")
                        .contact(new Contact().name("admin")));
    }
}
