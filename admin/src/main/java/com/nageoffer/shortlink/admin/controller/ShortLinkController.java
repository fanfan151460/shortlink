package com.nageoffer.shortlink.admin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.nageoffer.shortlink.admin.common.biz.user.SentinelBlockUtil;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.IRemoteShortLinkService;
import com.nageoffer.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "短链接管理", description = "短链接创建、查询、更新、删除")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1")
@Slf4j
public class ShortLinkController {
    private final IRemoteShortLinkService shortLinkService;

    @Operation(summary = "创建短链接", description = "传入原始URL，返回生成的短链接（MurmurHash+Base62）")
    @PostMapping("/create")
    @SentinelResource(value = "create_short-link",
            blockHandlerClass = SentinelBlockUtil.class,
            blockHandler = "createShortLinkBlock")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkReqDTO reqDTO) {
        return Results.success(shortLinkService.createShortLink(reqDTO));
    }

    @Operation(summary = "分页查询短链接", description = "按分组gid分页查询短链接列表")
    @GetMapping("/page")
    public Result<List<ShortLinkRespDTO>> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        log.info("调用分页短链接");
        return Results.success(shortLinkService.pageShortLink(linkPageReqDTO));
    }

    @Operation(summary = "更新短链接", description = "更新短链接的标题、有效期等元数据")
    @PutMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpReqDTO reqDTO) {
        shortLinkService.updateShortLink(reqDTO);
        return Results.success();
    }

    @Operation(summary = "删除短链接", description = "将短链接移入回收站（逻辑删除）")
    @DeleteMapping("/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleDTO recycleDTO) {
        shortLinkService.removeShortLink(recycleDTO);
        return Results.success();
    }
}
