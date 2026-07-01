package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.service.ILinkTitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "网页标题获取", description = "抓取原始URL的HTML标题")
@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class LinkTitleController {

    private final ILinkTitleService linkTitleService;

    @Operation(summary = "获取网页标题", description = "根据URL抓取目标页面的title标签内容")
    @GetMapping("/title")
    public Result<String> getTitleByUrl(@RequestParam String url) {
        return Results.success(linkTitleService.getTitleByUrl(url));
    }
}
