package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkAccessLogsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    @Insert("INSERT INTO t_link_access_logs (full_short_url, gid, user, ip, browser, os, network, device, locale, create_time, update_time, del_flag) "
            + "VALUES (#{log.fullShortUrl}, #{log.gid}, #{log.user}, #{log.ip}, #{log.browser}, #{log.os}, #{log.network}, #{log.device}, #{log.locale}, NOW(), NOW(), 0)")
    void insertAccessLog(@Param("log") LinkAccessLogsDO linkAccessLogsDO);

    @Select("<script> "
            + "SELECT "
            + "    user, "
            + "    CASE "
            + "        WHEN MIN(create_time) IS NULL THEN '新访客' "
            + "        WHEN MIN(create_time) BETWEEN #{startDate} AND #{endDate} THEN '新访客' "
            + "        ELSE '老访客' "
            + "    END AS uvType "
            + "FROM "
            + "    t_link_access_logs "
            + "WHERE "
            + "    full_short_url = #{fullShortUrl} "
            + "    AND gid = #{gid} "
            + "    AND user IN "
            + "    <foreach item='item' index='index' collection='userAccessLogsList' open='(' separator=',' close=')'> "
            + "        #{item} "
            + "    </foreach> "
            + "GROUP BY "
            + "    user"
            + "</script>")
    List<Map<String, String>> selectUvTypeGroupByUser(@Param("fullShortUrl") String fullShortUrl,
                                                   @Param("gid") String gid,
                                                   @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("userAccessLogsList") List<String> userAccessLogsList);
}
