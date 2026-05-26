package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class GroupLinkUpdateDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 组名
     */
    private String name;
}
