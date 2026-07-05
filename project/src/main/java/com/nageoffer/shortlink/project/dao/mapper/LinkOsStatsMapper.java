package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    @Update("INSERT INTO t_link_os_stats (full_short_url, date, cnt, os, create_time, update_time, del_flag) "
            + "VALUES (#{linkOsStats.fullShortUrl}, #{linkOsStats.date}, 1, #{linkOsStats.os}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1"
            )
    void insertLinkOsStats(@Param("linkOsStats") LinkOsStatsDO linkOsStatsDO);
}
