package com.nageoffer.shortlink.project.common.biz.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String userName;

    private String token;

    private String realName;

    private String userId;
}
