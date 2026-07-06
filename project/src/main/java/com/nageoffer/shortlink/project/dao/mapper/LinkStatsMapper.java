package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkStatsDO;
import com.nageoffer.shortlink.project.dto.resp.AccessStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkStatsMapper extends BaseMapper<LinkStatsDO> {

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

    @Select("SELECT date, SUM(pv) AS pv, SUM(uv) AS uv, SUM(uip) AS uip "
            + "FROM t_link_access_stats "
            + "WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY date")
    List<AccessStatsVO> selectAccessStats(@Param("fullShortUrl") String fullShortUrl,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
