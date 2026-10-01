package com.nageoffer.shortlink.admin.remote.impl;

import com.nageoffer.shortlink.admin.remote.IRemoteActivityService;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityLinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ActivityRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.service.IGroupService;
import com.nageoffer.shortlink.framework.exception.ClientException;
import com.nageoffer.shortlink.framework.exception.ServiceException;
import com.nageoffer.shortlink.framework.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteActivityServiceImpl implements IRemoteActivityService {

    private final ProjectFeignClient projectFeignClient;
    private final IGroupService groupService;

    @Override
    public void addNewActivity(ActivityReqDTO reqDTO) {
        if (!groupService.hasGid(reqDTO.getGid())) {
            throw new ClientException("分组不存在或不属于当前用户");
        }
        Result<Void> result = projectFeignClient.addNewActivity(reqDTO);
        if (!result.isSuccess()) {
            throw new ServiceException(result.getMessage());
        }
    }

    @Override
    public void updateActivity(ActivityUpdateReqDTO reqDTO) {
        Result<Void> result = projectFeignClient.updateActivity(reqDTO);
        if (!result.isSuccess()) {
            throw new ServiceException(result.getMessage());
        }
    }

    @Override
    public void removeActivity(Long id) {
        Result<Void> result = projectFeignClient.removeActivity(id);
        if (!result.isSuccess()) {
            throw new ServiceException(result.getMessage());
        }
    }

    @Override
    public List<ActivityRespDTO> pageActivity(ActivityPageReqDTO reqDTO) {
        Result<List<ActivityRespDTO>> result = projectFeignClient.pageActivity(
                reqDTO.getCurrent(),
                reqDTO.getSize(),
                reqDTO.getGid(),
                reqDTO.getStatus(),
                reqDTO.getActivityName()
        );
        if (!result.isSuccess()) {
            throw new ServiceException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<ShortLinkCreateRespDTO> addActivityLinks(ActivityLinkCreateReqDTO reqDTO) {
        Result<List<ShortLinkCreateRespDTO>> result = projectFeignClient.addActivityLinks(reqDTO);
        if (!result.isSuccess()) {
            throw new ServiceException(result.getMessage());
        }
        return result.getData();
    }
}
