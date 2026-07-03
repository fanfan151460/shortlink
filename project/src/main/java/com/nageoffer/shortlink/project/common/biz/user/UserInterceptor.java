package com.nageoffer.shortlink.project.common.biz.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String username = request.getHeader("username");
        String token = request.getHeader("token");

        UserContext.setUser(new UserInfoDTO()
                .setUserName(username)
                .setToken(token));
        return true;
    }


}
