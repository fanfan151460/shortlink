package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class GroupLinkOrderDTO {
    /**
     * gid
     */
    private String groupId;

    /**
     * 分组id
     */
    private Integer sortOrder;
}
