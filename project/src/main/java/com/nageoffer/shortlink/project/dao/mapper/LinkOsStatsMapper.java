package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkOsStatsDO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    @Update("INSERT INTO t_link_os_stats (full_short_url, date, cnt, os, create_time, update_time, del_flag) "
            + "VALUES (#{linkOsStats.fullShortUrl}, #{linkOsStats.date}, 1, #{linkOsStats.os}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1"
            )
    void insertLinkOsStats(@Param("linkOsStats") LinkOsStatsDO linkOsStatsDO);

    @Select("SELECT os AS name, SUM(cnt) AS value "
            + "FROM t_link_os_stats "
            + "WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY os")
    List<StatsItemVO> selectOsStats(@Param("fullShortUrl") String fullShortUrl,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

}
