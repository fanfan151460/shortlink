package com.nageoffer.shortlink.project.controller;


import com.nageoffer.shortlink.framework.result.Result;
import com.nageoffer.shortlink.framework.result.Results;
import com.nageoffer.shortlink.project.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.project.dto.resp.accessLogRespDTO;
import com.nageoffer.shortlink.project.service.ILinkAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "访问日志", description = "短链接访问记录的统计分析")
@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class LinkAccessController {
    private final ILinkAccessService linkAccessService;

    @Operation(summary = "查询访问日志", description = "按短链接查询访问记录，RocketMQ异步写入的8表统计数据")
    @PostMapping("/access-logs")
    public Result<List<accessLogRespDTO>> getAccessLogs(@RequestBody AccessLogReqDTO accessLogReqDTO) {
        List<accessLogRespDTO> listLog = linkAccessService.getAccessLogs(accessLogReqDTO);
        return Results.success(listLog);
    }
}
