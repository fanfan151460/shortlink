package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RecyclePageDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * gid
     */
    private String gid;

    /**
     * 页码
     */
    private int current;

    /**
     * 每页大小
     */
    private int size;
}
