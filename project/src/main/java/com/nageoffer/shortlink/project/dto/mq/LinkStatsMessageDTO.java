package com.nageoffer.shortlink.project.dto.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class LinkStatsMessageDTO implements Serializable {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 原始Cookie头
     */
    private String cookieHeader;

    /**
     * 客户端地址
     */
    private String remoteAddr;

    /**
     * User-Agent头
     */
    private String userAgent;
}
