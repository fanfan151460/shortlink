package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.service.ILinkTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class LinkTitleController {

    private final ILinkTitleService linkTitleService;

    @GetMapping("/title")
    public Result<String> getTitleByUrl(@RequestParam String url) {
        return Results.success(linkTitleService.getTitleByUrl(url));
    }
}
