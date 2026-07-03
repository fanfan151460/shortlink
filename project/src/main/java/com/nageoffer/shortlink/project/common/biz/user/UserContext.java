package com.nageoffer.shortlink.project.common.biz.user;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

public class UserContext {
    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static UserInfoDTO getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void setUser(UserInfoDTO userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }

    public static String getUserName() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get()).map(UserInfoDTO::getUserName).orElse(null);
    }
}
