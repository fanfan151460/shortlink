package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.ActivityStatsDO;
import com.nageoffer.shortlink.project.dto.resp.ActivityStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ActivityStatsMapper extends BaseMapper<ActivityStatsDO> {

    /**
     * uv = uv + #{stats.uv}：uv 传 0/1，由消费者侧 Redis Set 的 SADD 结果决定。
     * 与 LinkStatsMapper.insertLinkStats 同一套 upsert 写法。
     */
    @Update("INSERT INTO t_activity_stats (activity_id, date, uv, create_time, update_time, del_flag) "
            + "VALUES (#{stats.activityId}, #{stats.date}, #{stats.uv}, NOW(), NOW(), 0) "
            + "ON DUPLICATE KEY UPDATE "
            + "uv = uv + #{stats.uv}"
    )
    void insertActivityStats(@Param("stats") ActivityStatsDO statsDO);

    /** 活动维度去重 UV（按天）。t_activity_stats 是单表，无分片键要求。 */
    @Select("SELECT date, uv AS activityUv "
            + "FROM t_activity_stats "
            + "WHERE activity_id = #{activityId} AND date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY date")
    List<ActivityStatsVO> selectActivityUv(@Param("activityId") Long activityId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * 各渠道 UV 之和（按天）。必须带 l.gid —— 它既是权限维度也是 t_link 的分片键，
     * 不带会被 ShardingSphere 广播到 16 张分片表。
     */
    @Select("SELECT s.date, SUM(s.uv) AS channelUv "
            + "FROM t_link_access_stats s "
            + "JOIN t_link l ON l.full_short_url = s.full_short_url "
            + "WHERE l.gid = #{gid} AND l.activity_id = #{activityId} AND l.del_flag = 0 "
            + "AND s.date BETWEEN #{startDate} AND #{endDate} "
            + "GROUP BY s.date")
    List<ActivityStatsVO> selectChannelSumUv(@Param("gid") String gid,
                                             @Param("activityId") Long activityId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
