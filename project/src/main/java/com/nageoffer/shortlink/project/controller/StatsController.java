package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.StatsQueryReqDTO;
import com.nageoffer.shortlink.project.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.project.dto.resp.StatsDashboardRespDTO;
import com.nageoffer.shortlink.project.dto.resp.StatsItemVO;
import com.nageoffer.shortlink.project.service.IStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据统计", description = "短链接各维度访问统计")
@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class StatsController {

    private final IStatsService statsService;

    @Operation(summary = "统计仪表盘（批量）")
    @PostMapping("/stats/dashboard")
    public Result<StatsDashboardRespDTO> getDashboard(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getDashboard(reqDTO));
    }

    @Operation(summary = "地区统计")
    @PostMapping("/stats/locale")
    public Result<List<StatsItemVO>> getLocaleStats(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getLocaleStats(reqDTO));
    }

    @Operation(summary = "操作系统统计")
    @PostMapping("/stats/os")
    public Result<List<StatsItemVO>> getOsStats(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getOsStats(reqDTO));
    }

    @Operation(summary = "浏览器统计")
    @PostMapping("/stats/browser")
    public Result<List<StatsItemVO>> getBrowserStats(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getBrowserStats(reqDTO));
    }

    @Operation(summary = "设备统计")
    @PostMapping("/stats/device")
    public Result<List<StatsItemVO>> getDeviceStats(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getDeviceStats(reqDTO));
    }

    @Operation(summary = "网络统计")
    @PostMapping("/stats/network")
    public Result<List<StatsItemVO>> getNetworkStats(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getNetworkStats(reqDTO));
    }

    @Operation(summary = "访问统计")
    @PostMapping("/stats/access")
    public Result<List<AccessStatsVO>> getAccessStats(@RequestBody StatsQueryReqDTO reqDTO) {
        return Results.success(statsService.getAccessStats(reqDTO));
    }
}
