package com.nageoffer.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.dao.entity.ActivityDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.*;
import com.nageoffer.shortlink.project.dto.req.ActivityStatsQueryReqDTO;
import com.nageoffer.shortlink.project.dto.req.StatsQueryReqDTO;
import com.nageoffer.shortlink.project.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.project.dto.resp.ActivityStatsVO;
import com.nageoffer.shortlink.project.dto.resp.StatsDashboardRespDTO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import com.nageoffer.shortlink.project.service.IStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements IStatsService {
    private final ShortLinkMapper shortLinkMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkLocalStatsMapper linkLocalStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkStatsMapper linkStatsMapper;
    private final ActivityMapper activityMapper;
    private final ActivityStatsMapper activityStatsMapper;

    @Override
    public StatsDashboardRespDTO getDashboard(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return new StatsDashboardRespDTO();
        }
        String u = reqDTO.getFullShortUrl();
        StatsDashboardRespDTO dto = new StatsDashboardRespDTO();
        dto.setLocale(linkLocalStatsMapper.selectLocaleStats(u, reqDTO.getStartDate(), reqDTO.getEndDate()));
        dto.setOs(linkOsStatsMapper.selectOsStats(u, reqDTO.getStartDate(), reqDTO.getEndDate()));
        dto.setBrowser(linkBrowserStatsMapper.selectBrowserStats(u, reqDTO.getStartDate(), reqDTO.getEndDate()));
        dto.setDevice(linkDeviceStatsMapper.selectDeviceStats(u, reqDTO.getStartDate(), reqDTO.getEndDate()));
        dto.setNetwork(linkNetworkStatsMapper.selectNetworkStats(u, reqDTO.getStartDate(), reqDTO.getEndDate()));
        dto.setAccess(linkStatsMapper.selectAccessStats(u, reqDTO.getStartDate(), reqDTO.getEndDate()));
        return dto;
    }

    @Override
    public List<StatsItemVO> getLocaleStats(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return List.of();
        }
        return linkLocalStatsMapper.selectLocaleStats(reqDTO.getFullShortUrl(), reqDTO.getStartDate(), reqDTO.getEndDate());
    }

    @Override
    public List<StatsItemVO> getOsStats(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return List.of();
        }
        return linkOsStatsMapper.selectOsStats(reqDTO.getFullShortUrl(), reqDTO.getStartDate(), reqDTO.getEndDate());
    }

    @Override
    public List<StatsItemVO> getBrowserStats(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return List.of();
        }
        return linkBrowserStatsMapper.selectBrowserStats(reqDTO.getFullShortUrl(), reqDTO.getStartDate(), reqDTO.getEndDate());
    }

    @Override
    public List<StatsItemVO> getDeviceStats(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return List.of();
        }
        return linkDeviceStatsMapper.selectDeviceStats(reqDTO.getFullShortUrl(), reqDTO.getStartDate(), reqDTO.getEndDate());
    }

    @Override
    public List<StatsItemVO> getNetworkStats(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return List.of();
        }
        return linkNetworkStatsMapper.selectNetworkStats(reqDTO.getFullShortUrl(), reqDTO.getStartDate(), reqDTO.getEndDate());
    }

    @Override
    public List<AccessStatsVO> getAccessStats(StatsQueryReqDTO reqDTO) {
        ShortLinkDO shortLink = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName()));
        if (shortLink == null) {
            return List.of();
        }
        return linkStatsMapper.selectAccessStats(reqDTO.getFullShortUrl(), reqDTO.getStartDate(), reqDTO.getEndDate());
    }

    @Override
    public List<ActivityStatsVO> getActivityStats(ActivityStatsQueryReqDTO reqDTO) {
        // 归属校验：活动必须属于当前用户的当前分组。查不到就直接返回空（与上面各方法一致）。
        ActivityDO activity = activityMapper.selectOne(new LambdaQueryWrapper<ActivityDO>()
                .eq(ActivityDO::getId, reqDTO.getActivityId())
                .eq(ActivityDO::getGid, reqDTO.getGid())
                .eq(ActivityDO::getUserName, UserContext.getUserName()));
        if (activity == null) {
            return List.of();
        }
        List<ActivityStatsVO> activityLine = activityStatsMapper.selectActivityUv(
                reqDTO.getActivityId(), reqDTO.getStartDate(), reqDTO.getEndDate());
        List<ActivityStatsVO> channelLine = activityStatsMapper.selectChannelSumUv(
                reqDTO.getGid(), reqDTO.getActivityId(), reqDTO.getStartDate(), reqDTO.getEndDate());

        // 两条线按日期合并。某天只有一条线有数据时，另一条补 0 —— 前端画两条折线要对齐 x 轴。
        Map<LocalDate, ActivityStatsVO> merged = new LinkedHashMap<>();
        for (ActivityStatsVO vo : activityLine) {
            merged.computeIfAbsent(vo.getDate(), d -> new ActivityStatsVO().setDate(d))
                    .setActivityUv(vo.getActivityUv());
        }
        for (ActivityStatsVO vo : channelLine) {
            merged.computeIfAbsent(vo.getDate(), d -> new ActivityStatsVO().setDate(d))
                    .setChannelUv(vo.getChannelUv());
        }
        List<ActivityStatsVO> result = new ArrayList<>(merged.values());
        result.forEach(vo -> {
            if (vo.getActivityUv() == null) {
                vo.setActivityUv(0);
            }
            if (vo.getChannelUv() == null) {
                vo.setChannelUv(0);
            }
        });
        result.sort(Comparator.comparing(ActivityStatsVO::getDate));
        return result;
    }
}
