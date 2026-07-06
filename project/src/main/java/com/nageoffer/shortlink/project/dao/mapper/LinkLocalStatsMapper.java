package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkLocalStatsDO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkLocalStatsMapper extends BaseMapper<LinkLocalStatsDO> {

    @Update("INSERT INTO t_link_locale_stats (full_short_url, date, cnt, province, city, adcode, country, create_time, update_time, del_flag) "
            + "VALUES (#{linkLocalStats.fullShortUrl}, #{linkLocalStats.date}, 1, #{linkLocalStats.province}, #{linkLocalStats.city}, #{linkLocalStats.adcode}, #{linkLocalStats.country}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1"
            )
    void insertLinkLocalStats(@Param("linkLocalStats") LinkLocalStatsDO linkLocalStatsDO);

    @Select("SELECT province AS name, SUM(cnt) AS value "
            + "FROM t_link_locale_stats "
            + "WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY province")
    List<StatsItemVO> selectLocaleStats(@Param("fullShortUrl") String fullShortUrl,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}
