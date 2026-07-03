package com.nageoffer.shortlink.admin.common.biz.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.nageoffer.shortlink.admin.dto.req.UserLoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOGIN;

@Component
@RequiredArgsConstructor
public class UserLoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        Object loginDTO = stringRedisTemplate.opsForHash().get(LOGIN + username, token);
        UserLoginDTO userLoginDTO = JSONUtil.toBean((String) loginDTO, UserLoginDTO.class);
        UserInfoDTO userInfoDTO = BeanUtil.copyProperties(userLoginDTO, UserInfoDTO.class, "password");
        // 保存用户上下文
        UserContext.setUser(userInfoDTO);
        return true;
    }
}
