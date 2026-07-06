package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.remote.dto.req.StatsRemoteReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsDashboardVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsItemVO;

import java.util.List;

public interface IRemoteStatsService {

    /**
     * 远程查询统计仪表盘（批量）
     */
    StatsDashboardVO getDashboard(StatsRemoteReqDTO reqDTO);

    /**
     * 远程查询地区访问统计
     */
    List<StatsItemVO> getLocaleStats(StatsRemoteReqDTO reqDTO);

    /**
     * 远程查询操作系统访问统计
     */
    List<StatsItemVO> getOsStats(StatsRemoteReqDTO reqDTO);

    /**
     * 远程查询浏览器访问统计
     */
    List<StatsItemVO> getBrowserStats(StatsRemoteReqDTO reqDTO);

    /**
     * 远程查询设备访问统计
     */
    List<StatsItemVO> getDeviceStats(StatsRemoteReqDTO reqDTO);

    /**
     * 远程查询网络访问统计
     */
    List<StatsItemVO> getNetworkStats(StatsRemoteReqDTO reqDTO);

    /**
     * 远程查询按天PV/UV/UIP访问统计
     */
    List<AccessStatsVO> getAccessStats(StatsRemoteReqDTO reqDTO);
}
