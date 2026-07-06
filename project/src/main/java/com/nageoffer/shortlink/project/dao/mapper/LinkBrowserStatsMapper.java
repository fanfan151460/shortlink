package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {

    @Update("INSERT INTO t_link_browser_stats (full_short_url, date, cnt, browser, create_time, update_time, del_flag) "
            + "VALUES (#{linkBrowserStats.fullShortUrl}, #{linkBrowserStats.date}, 1, #{linkBrowserStats.browser}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1, "
            + "update_time = NOW()")
    void insertLinkBrowserStats(@Param("linkBrowserStats") LinkBrowserStatsDO linkBrowserStatsDO);

    @Select("SELECT browser AS name, SUM(cnt) AS value "
            + "FROM t_link_browser_stats "
            + "WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY browser")
    List<StatsItemVO> selectBrowserStats(@Param("fullShortUrl") String fullShortUrl,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
