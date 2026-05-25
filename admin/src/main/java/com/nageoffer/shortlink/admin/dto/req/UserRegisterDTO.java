package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRegisterDTO {
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String mail;

    private Long deletionTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean delFlag;


}
