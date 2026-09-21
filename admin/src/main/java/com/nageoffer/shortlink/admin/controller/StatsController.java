package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.remote.IRemoteStatsService;
import com.nageoffer.shortlink.admin.remote.dto.req.StatsRemoteReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsDashboardVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsItemVO;
import com.nageoffer.shortlink.framework.result.Result;
import com.nageoffer.shortlink.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "数据统计", description = "短链接各维度访问统计")
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class StatsController {

    private final IRemoteStatsService remoteStatsService;

    @Operation(summary = "统计仪表盘（批量）")
    @PostMapping("/stats/dashboard")
    public Result<StatsDashboardVO> getDashboard(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getDashboard(reqDTO));
    }

    @Operation(summary = "地区统计")
    @PostMapping("/stats/locale")
    public Result<List<StatsItemVO>> getLocaleStats(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getLocaleStats(reqDTO));
    }

    @Operation(summary = "操作系统统计")
    @PostMapping("/stats/os")
    public Result<List<StatsItemVO>> getOsStats(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getOsStats(reqDTO));
    }

    @Operation(summary = "浏览器统计")
    @PostMapping("/stats/browser")
    public Result<List<StatsItemVO>> getBrowserStats(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getBrowserStats(reqDTO));
    }

    @Operation(summary = "设备统计")
    @PostMapping("/stats/device")
    public Result<List<StatsItemVO>> getDeviceStats(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getDeviceStats(reqDTO));
    }

    @Operation(summary = "网络统计")
    @PostMapping("/stats/network")
    public Result<List<StatsItemVO>> getNetworkStats(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getNetworkStats(reqDTO));
    }

    @Operation(summary = "访问统计")
    @PostMapping("/stats/access")
    public Result<List<AccessStatsVO>> getAccessStats(@RequestBody StatsRemoteReqDTO reqDTO) {
        return Results.success(remoteStatsService.getAccessStats(reqDTO));
    }
}
