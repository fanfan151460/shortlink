package com.nageoffer.shortlink.admin.remote.impl;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.exception.ClientException;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.remote.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.RecycleBinShortLinkDTO;
import com.nageoffer.shortlink.admin.service.IGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteRecycleServiceImpl implements IRemoteRecycleService {

    private final IGroupService groupService;
    private final ProjectFeignClient projectFeignClient;

    @Override
    public void saveRecycleBin(RecycleDTO recycleDTO) {
        Result<Void> result = projectFeignClient.saveRecycleBin(recycleDTO);
        if (!result.isSuccess()) {
            throw new ClientException(result.getMessage());
        }
    }

    @Override
    public List<RecycleBinShortLinkDTO> pageRecycle(RecyclePageDTO pageReqDTO) {
        Result<List<RecycleBinShortLinkDTO>> result = projectFeignClient.pageRecycle(
                pageReqDTO.getGid(),
                pageReqDTO.getCurrent(),
                pageReqDTO.getSize()
        );
        if (!result.isSuccess()) {
            throw new ClientException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public void rmRecycleBin(RecycleDTO recycleDTO) {
        Result<Void> result = projectFeignClient.rmRecycleBin(recycleDTO);
        groupService.lambdaUpdate()
                .eq(GroupDO::getGid, recycleDTO.getGid())
                .eq(GroupDO::getDelFlag, 1)
                .set(GroupDO::getDelFlag, 0)
                .update();
        if (!result.isSuccess()) {
            throw new ClientException(result.getMessage());
        }
    }

    @Override
    public void saveRecycleBinAll(String gid) {
        Result<Void> result = projectFeignClient.saveRecycleBinAll(gid);
        if (!result.isSuccess()) {
            throw new ClientException(result.getMessage());
        }
    }
}
