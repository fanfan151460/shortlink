package com.nageoffer.shortlink.admin.config;

import com.nageoffer.shortlink.admin.common.biz.user.UserLoginInterceptor;
import com.nageoffer.shortlink.admin.common.biz.user.UserReflushInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class UserConfiguration implements WebMvcConfigurer {
    private final UserReflushInterceptor userReflushInterceptor;
    private final UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userReflushInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/short-link/admin/v1/user/login",
                        "/api/short-link/admin/v1/user",
                        "/api/short-link/admin/v1/user/has-username/**",
                        "/api/short-link/admin/v1/test"
                )
                .order(0);
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/short-link/admin/v1/user/login",
                        "/api/short-link/admin/v1/user",
                        "/api/short-link/admin/v1/user/has-username/**",
                        "/api/short-link/admin/v1/test"
                )
                .order(1);
    }
}
