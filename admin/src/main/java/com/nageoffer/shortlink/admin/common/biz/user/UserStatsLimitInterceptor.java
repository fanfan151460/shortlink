package com.nageoffer.shortlink.admin.common.biz.user;

import com.nageoffer.shortlink.admin.common.exception.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserStatsLimitInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> SCRIPT;

    static {
        SCRIPT = new DefaultRedisScript<>();
        SCRIPT.setLocation(new ClassPathResource("lua/user_stats_limit_lua.lua"));
        SCRIPT.setResultType(Long.class);
    }

    @Value("${short-link.user-limit.time}")
    private String timeWindow;

    @Value("${short-link.user-limit.max-click}")
    private long maxAccess;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = UserContext.getUsername();
        if (username == null) {
            return true;
        }
        Long count = stringRedisTemplate.execute(
                SCRIPT,
                List.of(username),
                timeWindow
        );
        if (count > maxAccess) {
            throw new ClientException("系统繁忙，请稍后再试");
        }
        return true;
    }
}
