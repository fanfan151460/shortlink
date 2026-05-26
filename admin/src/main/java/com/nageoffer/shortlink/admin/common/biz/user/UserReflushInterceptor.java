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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOGIN;

@Component
@RequiredArgsConstructor
public class UserReflushInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        if (Objects.equals(token, "4b312f0b-aa6a-474a-8c20-70b2544c8f97")) {
            return true;
        }
        Object UserLoginDTO = stringRedisTemplate.opsForHash().get(LOGIN + username, token);
        //反序列化
        UserLoginDTO userLoginDTO = JSONUtil.toBean((String) UserLoginDTO, UserLoginDTO.class);
        //保存到userContext
        UserInfoDTO userInfoDTO = BeanUtil.copyProperties(userLoginDTO, UserInfoDTO.class, "password");
        UserContext.setUser(userInfoDTO);
        //刷新有效期
        stringRedisTemplate.expire(LOGIN + username, 30, TimeUnit.HOURS);

        return true;
    }

}
