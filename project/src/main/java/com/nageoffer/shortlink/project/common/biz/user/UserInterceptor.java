package com.nageoffer.shortlink.project.common.biz.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String username = request.getHeader("username");

        UserContext.setUser(new UserInfoDTO()
                .setUserName(username));
        return true;
    }


}
