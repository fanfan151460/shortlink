package com.nageoffer.shortlink.admin.remote.dto.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RecycleBinShortLinkDTO {
    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 原链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 删除时间
     */
    private LocalDateTime updateTime;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 短链接描述
     */
    private String description;
}
