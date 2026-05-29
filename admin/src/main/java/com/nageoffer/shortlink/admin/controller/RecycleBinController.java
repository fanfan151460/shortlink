package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkRecycleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
