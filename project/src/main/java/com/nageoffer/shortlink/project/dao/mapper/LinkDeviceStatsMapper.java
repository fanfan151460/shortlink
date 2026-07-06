package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    @Update("INSERT INTO t_link_device_stats (full_short_url, date, cnt, device, create_time, update_time, del_flag) "
            + "VALUES (#{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.date}, 1, #{linkDeviceStats.device}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1, "
            + "update_time = NOW()")
    void insertLinkDeviceStats(@Param("linkDeviceStats") LinkDeviceStatsDO linkDeviceStatsDO);

    @Select("SELECT device AS name, SUM(cnt) AS value "
            + "FROM t_link_device_stats "
            + "WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY device")
    List<StatsItemVO> selectDeviceStats(@Param("fullShortUrl") String fullShortUrl,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}
