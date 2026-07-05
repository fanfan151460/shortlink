package com.nageoffer.shortlink.admin.remote.feign;

import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserInfoFeignInterceptor implements feign.RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String username = UserContext.getUsername();
        if (username != null) {
            requestTemplate.header("username", username);
        }
    }
}
