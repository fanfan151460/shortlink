package com.nageoffer.shortlink.admin.remote.impl;

import com.nageoffer.shortlink.admin.remote.IRemoteStatsService;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.dto.req.StatsRemoteReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsDashboardVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsItemVO;
import com.nageoffer.shortlink.framework.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteStatsServiceImpl implements IRemoteStatsService {

    private final ProjectFeignClient projectFeignClient;

    @Override
    public StatsDashboardVO getDashboard(StatsRemoteReqDTO reqDTO) {
        Result<StatsDashboardVO> result = projectFeignClient.getDashboard(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<StatsItemVO> getLocaleStats(StatsRemoteReqDTO reqDTO) {
        Result<List<StatsItemVO>> result = projectFeignClient.getLocaleStats(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<StatsItemVO> getOsStats(StatsRemoteReqDTO reqDTO) {
        Result<List<StatsItemVO>> result = projectFeignClient.getOsStats(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<StatsItemVO> getBrowserStats(StatsRemoteReqDTO reqDTO) {
        Result<List<StatsItemVO>> result = projectFeignClient.getBrowserStats(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<StatsItemVO> getDeviceStats(StatsRemoteReqDTO reqDTO) {
        Result<List<StatsItemVO>> result = projectFeignClient.getDeviceStats(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<StatsItemVO> getNetworkStats(StatsRemoteReqDTO reqDTO) {
        Result<List<StatsItemVO>> result = projectFeignClient.getNetworkStats(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<AccessStatsVO> getAccessStats(StatsRemoteReqDTO reqDTO) {
        Result<List<AccessStatsVO>> result = projectFeignClient.getAccessStats(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }
}
