package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // ========== AccessLogs ==========

    @GetMapping("/api/short-link/v1/access-logs")
    Result<List<accessLogRespDTO>> getAccessLogs(@RequestParam String fullShortUrl,
                                                  @RequestParam String gid,
                                                  @RequestParam Long current,
                                                  @RequestParam Long size,
                                                  @RequestParam LocalDate startDate,
                                                  @RequestParam LocalDate endDate);
}
