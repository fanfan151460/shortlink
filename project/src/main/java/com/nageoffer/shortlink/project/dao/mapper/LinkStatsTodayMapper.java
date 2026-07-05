package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {

    @Update("INSERT INTO t_link_stats_today (full_short_url, date, today_pv, today_uv, today_ip_count, create_time, update_time, del_flag) "
            + "VALUES (#{linkStatsToday.fullShortUrl}, #{linkStatsToday.date}, 1, 1, 1, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "today_pv = today_pv + 1, "
            + "today_uv = today_uv + #{linkStatsToday.todayUv}, "
            + "today_ip_count = today_ip_count + #{linkStatsToday.todayIpCount}, "
            + "update_time = NOW()")
    void insertLinkStatsToday(@Param("linkStatsToday") LinkStatsTodayDO linkStatsTodayDO);
}
