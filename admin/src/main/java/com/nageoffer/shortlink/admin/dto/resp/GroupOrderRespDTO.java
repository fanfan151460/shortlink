package com.nageoffer.shortlink.admin.dto.resp;

import lombok.Data;

@Data
public class GroupOrderRespDTO {
    /**
     * gid
     */
    private String groupId;

    /**
     * 分组id
     */
    private Integer sortOrder;
}
