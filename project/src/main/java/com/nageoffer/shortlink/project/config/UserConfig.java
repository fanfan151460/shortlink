package com.nageoffer.shortlink.project.config;

import com.nageoffer.shortlink.project.common.biz.user.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class UserConfig implements WebMvcConfigurer {
    private final UserInterceptor userInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/api/short-link/v1/**")
                .order(0);
    }
}
