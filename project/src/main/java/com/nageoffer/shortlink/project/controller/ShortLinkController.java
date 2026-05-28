package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.PageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class ShortLinkController {

    private final IShortLinkService shortLinkService;

    @PostMapping("/create")
    public Result<ShortLinkRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO) {
        ShortLinkRespDTO shortLinkRespDTO = shortLinkService.createShortLink(reqDTO);
        return Results.success(shortLinkRespDTO);
    }

    @GetMapping("/page")
    public Result<List<ShortLinkRespDTO>> pageShortLink(PageReqDTO pageReqDTO) {
        return Results.success(shortLinkService.pageShortLink(pageReqDTO));
    }

    @PutMapping()
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpReqDTO reqDTO) {
        shortLinkService.updateShortLink(reqDTO);
        return Results.success();
    }

    @GetMapping()
    public Result<Void> shortLinkGoTo(String shortLinkUri, ServletRequest request, ServletResponse response) {
        shortLinkService.gotoUrl(shortLinkUri, request, response);
        return Results.success();
    }
}
