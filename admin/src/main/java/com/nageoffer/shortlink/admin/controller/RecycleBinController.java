package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "回收站", description = "软删除短链接的恢复和分页查询")
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class RecycleBinController {

    private final IRemoteRecycleService remoteRecycleService;

    @Operation(summary = "移入回收站")
    @PostMapping("/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        remoteRecycleService.saveRecycleBin(recycleDTO);
        return Results.success();
    }

    @Operation(summary = "分页查询回收站")
    @GetMapping("/recycle-bin/page")
    public Result<List<ShortLinkRespDTO>> pageRecycle(RecyclePageDTO pageReqDTO) {
        return Results.success(remoteRecycleService.pageRecycle(pageReqDTO));
    }

    @Operation(summary = "从回收站恢复")
    @PostMapping("/recycle-bin/recover")
    public Result<Void> rmRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        remoteRecycleService.rmRecycleBin(recycleDTO);
        return Results.success();
    }


}
