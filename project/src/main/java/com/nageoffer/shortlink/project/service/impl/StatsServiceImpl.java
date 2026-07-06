package com.nageoffer.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.*;
import com.nageoffer.shortlink.project.dto.req.StatsQueryReqDTO;
import com.nageoffer.shortlink.project.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.project.dto.resp.StatsDashboardRespDTO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import com.nageoffer.shortlink.project.service.IStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
