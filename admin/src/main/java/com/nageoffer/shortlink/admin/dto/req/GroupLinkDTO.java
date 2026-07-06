package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class GroupLinkDTO {

    private String gid;

    private String name;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    private Integer delFlag;

}
