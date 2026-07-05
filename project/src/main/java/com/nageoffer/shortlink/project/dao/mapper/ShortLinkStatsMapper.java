package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShortLinkStatsMapper extends BaseMapper<LinkStatsDO> {

    @Update("INSERT INTO t_link_access_stats (full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) "
            + "VALUES (#{linkStats.fullShortUrl}, #{linkStats.date}, 1, 1, 1, #{linkStats.hour}, #{linkStats.weekday}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "pv = pv + 1, "
            + "uv = uv + #{linkStats.uv}, "
            + "uip = uip + #{linkStats.uip}, "
            + "hour = #{linkStats.hour}, "
            + "weekday = #{linkStats.weekday}"
    )
    void insertLinkStats(@Param("linkStats") LinkStatsDO linkStatsDO);
}
