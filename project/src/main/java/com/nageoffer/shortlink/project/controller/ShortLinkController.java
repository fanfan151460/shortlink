package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
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
@RequestMapping("")
@RequiredArgsConstructor
public class ShortLinkController {

    private final IShortLinkService shortLinkService;

    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO) {
        ShortLinkRespDTO shortLinkRespDTO = shortLinkService.createShortLink(reqDTO);
        return Results.success(shortLinkRespDTO);
    }

    @GetMapping("/api/short-link/v1/page")
    public Result<List<ShortLinkRespDTO>> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        return Results.success(shortLinkService.pageShortLink(linkPageReqDTO));
    }

    @PutMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpReqDTO reqDTO) {
        shortLinkService.updateShortLink(reqDTO);
        return Results.success();
    }

    @GetMapping("/{shortLinkUri}")
    public Result<Void> shortLinkGoTo(@PathVariable("shortLinkUri") String shortLinkUri, ServletRequest request, ServletResponse response) {
        shortLinkService.gotoOriginUrl(shortLinkUri, request, response);
        return Results.success();
    }

    @DeleteMapping("/api/short-link/v1/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleDTO recycleDTO) {
        shortLinkService.removeShortLink(recycleDTO);
        return Results.success();
    }
}
