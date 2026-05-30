package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkLocalStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LinkLocalStatsMapper extends BaseMapper<LinkLocalStatsDO> {

    @Update("INSERT INTO t_link_locale_stats (full_short_url, gid, date, cnt, province, city, adcode, country, create_time, update_time, del_flag) "
            + "VALUES (#{linkLocalStats.fullShortUrl}, #{linkLocalStats.gid}, #{linkLocalStats.date}, 1, #{linkLocalStats.province}, #{linkLocalStats.city}, #{linkLocalStats.adcode}, #{linkLocalStats.country}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1"
            )
    void insertLinkLocalStats(@Param("linkLocalStats") LinkLocalStatsDO linkLocalStatsDO);
}
