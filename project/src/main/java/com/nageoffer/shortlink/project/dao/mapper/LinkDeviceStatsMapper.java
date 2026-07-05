package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    @Update("INSERT INTO t_link_device_stats (full_short_url, date, cnt, device, create_time, update_time, del_flag) "
            + "VALUES (#{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.date}, 1, #{linkDeviceStats.device}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1, "
            + "update_time = NOW()")
    void insertLinkDeviceStats(@Param("linkDeviceStats") LinkDeviceStatsDO linkDeviceStatsDO);
}
