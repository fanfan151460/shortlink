package com.nageoffer.shortlink.admin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.nageoffer.shortlink.admin.common.biz.user.SentinelBlockUtil;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteShortLinkService;
import com.nageoffer.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/short-link/admin/v1")
public class ShortLinkController {
    private final IRemoteShortLinkService shortLinkService;

    @PostMapping("/create")
    @SentinelResource(value = "create_short-link",
            blockHandlerClass = SentinelBlockUtil.class,
            blockHandler = "createShortLinkBlock")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO) {
        return Results.success(shortLinkService.createShortLink(reqDTO));
    }

    @GetMapping("/page")
    public Result<List<ShortLinkRespDTO>> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        return Results.success(shortLinkService.pageShortLink(linkPageReqDTO));
    }

    @PutMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpReqDTO reqDTO) {
        shortLinkService.updateShortLink(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleDTO recycleDTO) {
        shortLinkService.removeShortLink(recycleDTO);
        return Results.success();
    }
}
