package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.project.dto.resp.accessLogRespDTO;
import com.nageoffer.shortlink.project.service.ILinkAccessLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class LinkAccessLogsController {
    private final ILinkAccessLogsService linkAccessLogsService;

    @GetMapping("/access-logs")
    public Result<List<accessLogRespDTO>> getAccessLogs(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            AccessLogReqDTO accessLogReqDTO) {
        accessLogReqDTO.setCurrent(current);
        accessLogReqDTO.setSize(size);
        List<accessLogRespDTO> listLog = linkAccessLogsService.getAccessLogs(accessLogReqDTO);
        return Results.success(listLog);
    }
}
