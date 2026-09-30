package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.framework.result.Result;
import com.nageoffer.shortlink.framework.result.Results;
import com.nageoffer.shortlink.project.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import com.nageoffer.shortlink.project.service.impl.ShortLinkServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "短链接核心服务", description = "创建、重定向、分页查询、更新、删除")
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final IShortLinkService shortLinkService;

    @Operation(summary = "创建短链接", description = "MurmurHash+Base62生成，布隆判重，双写MySQL分表，Redis缓存预热")
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO) {
        ShortLinkCreateRespDTO shortLinkRespDTO = shortLinkService.createShortLink(reqDTO);
        return Results.success(shortLinkRespDTO);
    }

    @Operation(summary = "创建短链接(分布式锁版)", description = "使用Redis分布式锁防并发重复创建")
    @PostMapping("/api/short-link/v1/create/lock")
    public Result<ShortLinkCreateRespDTO> createShortLinkWithLock(@RequestBody ShortLinkReqDTO reqDTO) {
        ShortLinkCreateRespDTO shortLinkRespDTO = ((ShortLinkServiceImpl) shortLinkService).createShortLinkByLock(reqDTO);
        return Results.success(shortLinkRespDTO);
    }

    @Operation(summary = "分页查询短链接", description = "按gid分页查询，ShardingSphere路由到对应分表")
    @GetMapping("/api/short-link/v1/page")
    public Result<List<ShortLinkRespDTO>> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        return Results.success(shortLinkService.pageShortLink(linkPageReqDTO));
    }

    @Operation(summary = "更新短链接", description = "读写锁保护，更新元数据（有效期、描述等）")
    @PutMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpReqDTO reqDTO) {
        shortLinkService.updateShortLink(reqDTO);
        return Results.success();
    }

    @Operation(summary = "短链接重定向", description = "四层缓存穿透防护：Redis → 布隆 → 空值缓存 → 分布式锁+MySQL，命中后302跳转")
    @GetMapping("/{shortLinkUri}")
    public void shortLinkGoTo(@PathVariable("shortLinkUri") String shortLinkUri, ServletRequest request, ServletResponse response) {
        shortLinkService.gotoOriginUrl(shortLinkUri, request, response);
    }

    @Operation(summary = "删除短链接", description = "软删除移入回收站")
    @DeleteMapping("/api/short-link/v1/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleDTO recycleDTO) {
        shortLinkService.removeShortLink(recycleDTO);
        return Results.success();
    }
}
