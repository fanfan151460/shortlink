package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ActivityStatsQueryReqDTO {

    /**
     * 分组标识。既是权限维度，也是 t_link 的分片键，必传
     */
    private String gid;

    private Long activityId;

    private LocalDate startDate;

    private LocalDate endDate;
}
