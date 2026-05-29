package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkRecycleDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IRecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/v1/recycle-bin")
@RequiredArgsConstructor
public class RecycleBinController {
    private final IRecycleBinService recycleBinService;

    @PostMapping("/save")
    public Result<Void> saveRecycleBin(@RequestBody ShortLinkRecycleDTO recycleDTO) {
        recycleBinService.saveRecycleBin(recycleDTO);
        return Results.success();
    }

    @GetMapping("/page")
    public Result<List<ShortLinkRespDTO>> pageShortLink(RecyclePageDTO pageReqDTO) {
        return Results.success(recycleBinService.pageRecycle(pageReqDTO));
    }

}
