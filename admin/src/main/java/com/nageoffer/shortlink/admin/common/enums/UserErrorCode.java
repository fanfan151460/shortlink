package com.nageoffer.shortlink.admin.common.enums;

import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCode implements IErrorCode {

    USER_NOTES("A00001", "用户信息为空"),

    USER_HAD("A00002","用户已存在");
    public final String code;
    public final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
