package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.project.dto.resp.RecycleBinShortLinkDTO;
import com.nageoffer.shortlink.project.service.IRecycleBinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "回收站", description = "软删除、分页查询、恢复、物理删除")
@RestController
@RequestMapping("/api/short-link/v1/recycle-bin")
@RequiredArgsConstructor
public class RecycleBinController {
    private final IRecycleBinService recycleBinService;

    @Operation(summary = "移入回收站")
    @PostMapping("/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        recycleBinService.saveRecycleBin(recycleDTO);
        return Results.success();
    }

    @GetMapping("/save-all/{gid}")
    public Result<Void> multiSaveRecycleBin(@PathVariable String gid) {
        recycleBinService.saveRecycleBinAll(gid);
        return Results.success();
    }

    @Operation(summary = "分页查询回收站")
    @GetMapping("/page")
    public Result<List<RecycleBinShortLinkDTO>> pageShortLink(RecyclePageDTO pageReqDTO) {
        return Results.success(recycleBinService.pageRecycle(pageReqDTO));
    }

    @Operation(summary = "从回收站恢复")
    @PostMapping("/recover")
    public Result<Void> rmRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        recycleBinService.rmRecycleBin(recycleDTO);
        return Results.success();
    }

    @Operation(summary = "彻底删除", description = "物理删除，不可恢复")
    @DeleteMapping("/dellete")
    public Result<Void> delRecycleBin(@RequestBody RecycleDTO recycleDTO) {
        recycleBinService.removeShortLink(recycleDTO);
        return Results.success();
    }
}
