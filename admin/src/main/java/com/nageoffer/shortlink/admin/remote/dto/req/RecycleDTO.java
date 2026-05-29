package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RecycleDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * gid
     */
    private String gid;
}
