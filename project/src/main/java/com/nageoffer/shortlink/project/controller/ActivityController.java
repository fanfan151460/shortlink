package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.framework.result.Result;
import com.nageoffer.shortlink.framework.result.Results;
import com.nageoffer.shortlink.project.dto.req.ActivityLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ActivityRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.service.IActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "营销活动", description = "活动创建、更新、删除、分页查询")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/v1/activity")
public class ActivityController {

    private final IActivityService activityService;

    @Operation(summary = "创建营销活动", description = "校验目标链接域名黑名单后落库")
    @PostMapping
    public Result<Void> addNewActivity(@RequestBody ActivityReqDTO reqDTO) {
        activityService.addNewActivity(reqDTO);
        return Results.success();
    }

    @Operation(summary = "更新营销活动", description = "改名称、目标链接、状态；gid 不可改")
    @PutMapping
    public Result<Void> updateActivity(@RequestBody ActivityUpdateReqDTO reqDTO) {
        activityService.updateActivity(reqDTO);
        return Results.success();
    }

    @Operation(summary = "删除营销活动", description = "逻辑删除，不影响该活动下的渠道短链")
    @DeleteMapping
    public Result<Void> removeActivity(@RequestParam Long id) {
        activityService.removeActivity(id);
        return Results.success();
    }

    @Operation(summary = "分页查询活动", description = "按当前用户查询活动，支持 gid、状态、名称模糊筛选")
    @GetMapping("/page")
    public Result<List<ActivityRespDTO>> pageActivity(ActivityPageReqDTO reqDTO) {
        return Results.success(activityService.pageActivity(reqDTO));
    }

    @Operation(summary = "批量创建渠道短链", description = "按渠道列表逐个创建短链，目标链接与分组取自活动本身")
    @PostMapping("/links")
    public Result<List<ShortLinkCreateRespDTO>> addActivityLinks(@RequestBody ActivityLinkCreateReqDTO reqDTO) {
        return Results.success(activityService.addActivityLinks(reqDTO));
    }

}
