package com.nageoffer.shortlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nageoffer.shortlink.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

/**
 * 用户响应层
 */
@Data
public class UserDTO {
    //用户id
    private Long id;
    //用户昵称
    private String username;

    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    //邮箱地址
    private String mail;
    //真实姓名
    private String realName;
}
