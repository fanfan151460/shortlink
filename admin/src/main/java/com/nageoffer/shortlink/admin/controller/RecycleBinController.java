package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.RecycleBinShortLinkDTO;
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
    private final ProjectFeignClient projectFeignClient;

    @Operation(summary = "移入回收站")
    @PostMapping("/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        remoteRecycleService.saveRecycleBin(recycleDTO);
        return Results.success();
    }

    @GetMapping("/recycle-bin/save-all/{gid}")
    public Result<Boolean> multiSaveRecycleBin(@PathVariable String gid) {
        return Results.success(remoteRecycleService.saveRecycleBinAll(gid));
    }


    @Operation(summary = "分页查询回收站")
    @GetMapping("/recycle-bin/page")
    public Result<List<RecycleBinShortLinkDTO>> pageRecycle(RecyclePageDTO pageReqDTO) {
        return Results.success(remoteRecycleService.pageRecycle(pageReqDTO));
    }

    @Operation(summary = "从回收站恢复")
    @PostMapping("/recycle-bin/recover")
    public Result<Void> rmRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        remoteRecycleService.rmRecycleBin(recycleDTO);
        return Results.success();
    }

    @Operation(summary = "永久删除短链接")
    @DeleteMapping("/recycle-bin/delete")
    public Result<Void> deleteRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        projectFeignClient.deleteRecycleBin(recycleDTO);
        return Results.success();
    }


}
