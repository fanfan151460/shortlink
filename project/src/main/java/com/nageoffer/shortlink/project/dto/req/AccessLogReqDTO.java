package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccessLogReqDTO {
    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页条数
     */
    private Long size;

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

}
