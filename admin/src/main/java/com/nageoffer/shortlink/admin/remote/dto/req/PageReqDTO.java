package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 短链接分页查询
 */
@Data
@Accessors(chain = true)
public class PageReqDTO {

    /**
     * 页码
     */
    private int current;
    /**
     * 每页大小
     */
    private int size;
    /**
     * gid
     */
    private String gid;
}
