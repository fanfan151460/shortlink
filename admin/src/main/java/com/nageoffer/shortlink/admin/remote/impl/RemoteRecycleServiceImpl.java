package com.nageoffer.shortlink.admin.remote.impl;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteRecycleServiceImpl implements IRemoteRecycleService {

    private final ProjectFeignClient projectFeignClient;

    @Override
    public void saveRecycleBin(RecycleDTO recycleDTO) {
        Result<Void> result = projectFeignClient.saveRecycleBin(recycleDTO);
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程移入回收站失败");
        }
    }

    @Override
    public List<ShortLinkRespDTO> pageRecycle(RecyclePageDTO pageReqDTO) {
        Result<List<ShortLinkRespDTO>> result = projectFeignClient.pageRecycle(
                pageReqDTO.getGid(),
                pageReqDTO.getCurrent(),
                pageReqDTO.getSize()
        );
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程回收站分页查询失败");
        }
        return result.getData();
    }

    @Override
    public void rmRecycleBin(RecycleDTO recycleDTO) {
        Result<Void> result = projectFeignClient.rmRecycleBin(recycleDTO);
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程恢复短链接失败");
        }
    }
}
