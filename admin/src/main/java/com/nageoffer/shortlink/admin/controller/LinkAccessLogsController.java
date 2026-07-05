package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.IRemoteAccessLogsService;
import com.nageoffer.shortlink.admin.remote.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "访问日志", description = "短链接访问记录的查询")
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class LinkAccessLogsController {
    private final IRemoteAccessLogsService remoteAccessLogsService;

    @Operation(summary = "查询访问记录", description = "按短链接查询访问日志，支持分页")
    @PostMapping("/stats/access-record")
    public Result<List<accessLogRespDTO>> getAccessLogs(@RequestBody AccessLogReqDTO accessLogReqDTO) {
        return Results.success(remoteAccessLogsService.getAccessLogs(accessLogReqDTO));
    }
}
