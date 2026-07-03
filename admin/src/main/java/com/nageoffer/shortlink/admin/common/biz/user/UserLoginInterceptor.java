package com.nageoffer.shortlink.admin.common.biz.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        // 保存用户上下文
        UserContext.setUser(new UserInfoDTO().setUsername(username).setToken(token));
        return true;
    }
}
