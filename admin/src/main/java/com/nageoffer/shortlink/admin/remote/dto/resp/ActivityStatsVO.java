package com.nageoffer.shortlink.admin.remote.dto.resp;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ActivityStatsVO {

    private LocalDate date;

    /**
     * 活动去重 UV
     */
    private Integer activityUv;

    /**
     * 各渠道 UV 之和
     */
    private Integer channelUv;
}
