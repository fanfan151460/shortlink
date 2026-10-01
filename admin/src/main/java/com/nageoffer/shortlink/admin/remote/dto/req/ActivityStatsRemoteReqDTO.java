package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ActivityStatsRemoteReqDTO {

    private String gid;

    private Long activityId;

    private LocalDate startDate;

    private LocalDate endDate;
}
