package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    @Update("INSERT INTO t_link_network_stats (full_short_url, date, cnt, network, create_time, update_time, del_flag) "
            + "VALUES (#{linkNetworkStats.fullShortUrl}, #{linkNetworkStats.date}, 1, #{linkNetworkStats.network}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "cnt = cnt + 1, "
            + "update_time = NOW()")
    void insertLinkNetworkStats(@Param("linkNetworkStats") LinkNetworkStatsDO linkNetworkStatsDO);

    @Select("SELECT network AS name, SUM(cnt) AS value "
            + "FROM t_link_network_stats "
            + "WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY network")
    List<StatsItemVO> selectNetworkStats(@Param("fullShortUrl") String fullShortUrl,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
