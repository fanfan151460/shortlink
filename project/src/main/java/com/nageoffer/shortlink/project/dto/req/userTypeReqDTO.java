package com.nageoffer.shortlink.project.dto.req;

import java.time.LocalDate;
import java.util.List;

public class userTypeReqDTO {
    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 用户列表
     */
    private List<String> userList;
}
