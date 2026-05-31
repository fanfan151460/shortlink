package com.nageoffer.shortlink.admin.remote.dto.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShortLinkRespDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 历史pv
     */
    private Integer totalPv;

    /**
     * 历史uv
     */
    private Integer totalUv;

    /**
     * 历史uip
     */
    private Integer totalUip;
}
