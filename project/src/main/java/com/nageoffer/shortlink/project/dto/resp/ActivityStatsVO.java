package com.nageoffer.shortlink.project.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 活动统计单日数据
 * <p>
 * 两条线口径一致（都是"每日去重数之和"），可直接对比：
 * activityUv 为活动维度去重（同一访客当天多次触达只算一次），channelUv 为各渠道 UV 之和。
 * 差额 = 当天被多渠道重复触达的访客数。
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatsVO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 活动去重 UV（来自 t_activity_stats）
     */
    private Integer activityUv;

    /**
     * 各渠道 UV 之和（来自 t_link_access_stats）
     */
    private Integer channelUv;
}
