package com.nageoffer.shortlink.admin.common.biz.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOGIN;

@Component
@RequiredArgsConstructor
public class UserReflushInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String username = request.getHeader("username");
        //刷新有效期
        stringRedisTemplate.expire(LOGIN + username, 30, TimeUnit.MINUTES);
        return true;
    }
}
