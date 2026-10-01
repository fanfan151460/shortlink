package com.nageoffer.shortlink.project.service;

import com.nageoffer.shortlink.project.dto.req.ActivityStatsQueryReqDTO;
import com.nageoffer.shortlink.project.dto.req.StatsQueryReqDTO;
import com.nageoffer.shortlink.project.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.project.dto.resp.ActivityStatsVO;
import com.nageoffer.shortlink.project.dto.resp.StatsDashboardRespDTO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;

import java.util.List;

public interface IStatsService {

    /**
     * 统计仪表盘：一次查询返回全部6个维度
     */
    StatsDashboardRespDTO getDashboard(StatsQueryReqDTO reqDTO);

    /**
     * 按省份聚合地区访问统计
     */
    List<StatsItemVO> getLocaleStats(StatsQueryReqDTO reqDTO);

    /**
     * 按操作系统聚合访问统计
     */
    List<StatsItemVO> getOsStats(StatsQueryReqDTO reqDTO);

    /**
     * 按浏览器聚合访问统计
     */
    List<StatsItemVO> getBrowserStats(StatsQueryReqDTO reqDTO);

    /**
     * 按设备聚合访问统计
     */
    List<StatsItemVO> getDeviceStats(StatsQueryReqDTO reqDTO);

    /**
     * 按网络聚合访问统计
     */
    List<StatsItemVO> getNetworkStats(StatsQueryReqDTO reqDTO);

    /**
     * 按天聚合PV/UV/UIP访问统计
     */
    List<AccessStatsVO> getAccessStats(StatsQueryReqDTO reqDTO);

    /**
     * 活动级跨渠道去重统计：按天返回「活动去重 UV」与「各渠道 UV 之和」两条线
     */
    List<ActivityStatsVO> getActivityStats(ActivityStatsQueryReqDTO reqDTO);
}
