package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 短链接分页查询
 */
@Data
@Accessors(chain = true)
public class LinkPageReqDTO {

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

    /**
     * 排序字段
     */
    private String orderFlag;
}
