package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkOrderDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkUpdateDTO;
import com.nageoffer.shortlink.admin.service.IGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分组管理", description = "短链接分组的CRUD和排序")
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class GroupController {
    private final IGroupService groupService;

    @Operation(summary = "创建分组")
    @PostMapping("/group")
    public Result<Void> addGroup(@RequestBody GroupLinkDTO groupLinkDTO) {
        String groupName = groupLinkDTO.getName();
        groupService.saveGroup(groupName);
        return Results.success();
    }

    @Operation(summary = "查询分组列表")
    @GetMapping("/group")
    public Result<List<GroupLinkDTO>> getGroups() {
        List<GroupLinkDTO> groupLinkDTOS = groupService.listGroup();
        return Results.success(groupLinkDTOS);
    }

    @Operation(summary = "查询已删除分组列表")
    @GetMapping("/group/deleted")
    public Result<List<GroupLinkDTO>> getAllGroups() {
        List<GroupLinkDTO> groupLinkDTOS = groupService.listAllGroup();
        return Results.success(groupLinkDTOS);
    }

    @Operation(summary = "更新分组名称")
    @PutMapping("/group")
    public Result<Void> updateGroup(@RequestBody GroupLinkUpdateDTO groupLinkUpdateDTO) {
        groupService.updateGroup(groupLinkUpdateDTO);
        return Results.success();
    }

    @Operation(summary = "删除分组")
    @DeleteMapping("/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.delGroup(gid);
        return Results.success();
    }

    @Operation(summary = "分组排序", description = "拖拽排序后提交新的分组顺序")
    @PostMapping("/group/sort")
    public Result<List<GroupLinkDTO>> updateGroupOrder(@RequestBody List<GroupLinkOrderDTO> linkOrderDTOList) {
        List<GroupLinkDTO> list = groupService.order(linkOrderDTOList);
        return Results.success(list);
    }
}
