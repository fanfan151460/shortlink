package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteAccessLogsService;
import com.nageoffer.shortlink.admin.remote.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class LinkAccessLogsController {
    private final IRemoteAccessLogsService remoteAccessLogsService;

    @GetMapping("/stats/access-record")
    public Result<List<accessLogRespDTO>> getAccessLogs(AccessLogReqDTO accessLogReqDTO) {
        if (accessLogReqDTO.getCurrent() == null) {
            accessLogReqDTO.setCurrent(1L);
        }
        if (accessLogReqDTO.getSize() == null) {
            accessLogReqDTO.setSize(10L);
        }
        return Results.success(remoteAccessLogsService.getAccessLogs(accessLogReqDTO));
    }
}
