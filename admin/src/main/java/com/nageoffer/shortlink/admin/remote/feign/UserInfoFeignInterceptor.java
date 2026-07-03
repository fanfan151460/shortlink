package com.nageoffer.shortlink.admin.remote.feign;

import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserInfoFeignInterceptor implements feign.RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        if(username != null && token != null) {
            requestTemplate.header("username", username);
            requestTemplate.header("token", token);
        }
    }
}
