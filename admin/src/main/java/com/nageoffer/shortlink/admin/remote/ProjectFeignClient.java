package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.StatsRemoteReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.AccessStatsVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsDashboardVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.StatsItemVO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "shortlink-project")
public interface ProjectFeignClient {

    // ========== ShortLink ==========

    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO);

    @GetMapping("/api/short-link/v1/page")
    Result<List<ShortLinkRespDTO>> pageShortLink(@RequestParam String gid,
                                                  @RequestParam Long current,
                                                  @RequestParam Long size,
                                                  @RequestParam(required = false) String orderFlag);

    @PutMapping("/api/short-link/v1/update")
    Result<Void> updateShortLink(@RequestBody ShortLinkUpReqDTO reqDTO);

    @DeleteMapping("/api/short-link/v1/remove")
    Result<Void> removeShortLink(@RequestBody RecycleDTO recycleDTO);

    // ========== Recycle ==========

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    Result<Void> saveRecycleBin(@RequestBody RecycleDTO recycleDTO);

    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<List<ShortLinkRespDTO>> pageRecycle(@RequestParam String gid,
                                                @RequestParam Long current,
                                                @RequestParam Long size);

    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    Result<Void> rmRecycleBin(@RequestBody RecycleDTO recycleDTO);

    // ========== Recycle (永久删除) ==========

    @DeleteMapping("/api/short-link/v1/recycle-bin/dellete")
    Result<Void> deleteRecycleBin(@RequestBody RecycleDTO recycleDTO);

    // ========== Title ==========

    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam String url);

    // ========== AccessLogs ==========

    @PostMapping("/api/short-link/v1/access-logs")
    Result<List<accessLogRespDTO>> getAccessLogs(@RequestBody AccessLogReqDTO reqDTO);

    // ========== Stats ==========

    @PostMapping("/api/short-link/v1/stats/dashboard")
    Result<StatsDashboardVO> getDashboard(@RequestBody StatsRemoteReqDTO reqDTO);

    @PostMapping("/api/short-link/v1/stats/locale")
    Result<List<StatsItemVO>> getLocaleStats(@RequestBody StatsRemoteReqDTO reqDTO);

    @PostMapping("/api/short-link/v1/stats/os")
    Result<List<StatsItemVO>> getOsStats(@RequestBody StatsRemoteReqDTO reqDTO);

    @PostMapping("/api/short-link/v1/stats/browser")
    Result<List<StatsItemVO>> getBrowserStats(@RequestBody StatsRemoteReqDTO reqDTO);

    @PostMapping("/api/short-link/v1/stats/device")
    Result<List<StatsItemVO>> getDeviceStats(@RequestBody StatsRemoteReqDTO reqDTO);

    @PostMapping("/api/short-link/v1/stats/network")
    Result<List<StatsItemVO>> getNetworkStats(@RequestBody StatsRemoteReqDTO reqDTO);

    @PostMapping("/api/short-link/v1/stats/access")
    Result<List<AccessStatsVO>> getAccessStats(@RequestBody StatsRemoteReqDTO reqDTO);
}
