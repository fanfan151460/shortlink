package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteShortLinkService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/short-link/admin/v1")
public class ShortLinkController {
    private final IRemoteShortLinkService shortLinkService;

    @PostMapping("/create")
    public Result<ShortLinkRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO) {
        ShortLinkRespDTO shortLinkRespDTO = shortLinkService.createShortLink(reqDTO);
        return Results.success(shortLinkRespDTO);
    }
}
