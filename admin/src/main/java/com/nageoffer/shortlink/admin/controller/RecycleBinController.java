package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkRecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class RecycleBinController {

    private final IRemoteRecycleService remoteRecycleService;

    @PostMapping("/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody ShortLinkRecycleDTO recycleDTO) {
        remoteRecycleService.saveRecycleBin(recycleDTO);
        return Results.success();
    }

    @GetMapping("/recycle-bin/page")
    public Result<List<ShortLinkRespDTO>> pageRecycle(RecyclePageDTO pageReqDTO) {
        return Results.success(remoteRecycleService.pageRecycle(pageReqDTO));
    }
}
