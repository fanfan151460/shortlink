package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkOrderDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkUpdateDTO;
import com.nageoffer.shortlink.admin.service.IGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class GroupController {
    private final IGroupService groupService;

    @PostMapping("/group")
    public Result<Void> addGroup(@RequestBody GroupLinkDTO groupLinkDTO) {
        String groupName = groupLinkDTO.getName();
        groupService.saveGroup(groupName);
        return Results.success();
    }

    @GetMapping("/group")
    public Result<List<GroupLinkDTO>> getGroups() {
        List<GroupLinkDTO> groupLinkDTOS = groupService.listGroup();
        return Results.success(groupLinkDTOS);
    }

    @PutMapping("/group")
    public Result<Void> updateGroup(@RequestBody GroupLinkUpdateDTO groupLinkUpdateDTO) {
        groupService.updateGroup(groupLinkUpdateDTO);
        return Results.success();
    }

    @DeleteMapping("/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.delGroup(gid);
        return Results.success();
    }

    @PostMapping("/group/sort")
    public Result<List<GroupLinkDTO>> updateGroupOrder(@RequestBody List<GroupLinkOrderDTO> linkOrderDTOList) {
        List<GroupLinkDTO> list = groupService.order(linkOrderDTOList);
        return Results.success(list);
    }
}
