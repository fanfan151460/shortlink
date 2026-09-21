package com.nageoffer.shortlink.admin.remote.impl;

import com.nageoffer.shortlink.admin.remote.IRemoteShortLinkService;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.admin.service.IGroupService;
import com.nageoffer.shortlink.framework.exception.ClientException;
import com.nageoffer.shortlink.framework.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteShortLinkServiceImpl implements IRemoteShortLinkService {

    private final ProjectFeignClient projectFeignClient;
    private final IGroupService groupService;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkReqDTO reqDTO) {
        if (!groupService.hasGid(reqDTO.getGid())) {
            throw new ClientException("分组不存在或不属于当前用户");
        }
        Result<ShortLinkCreateRespDTO> result = projectFeignClient.createShortLink(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<ShortLinkRespDTO> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        Result<List<ShortLinkRespDTO>> result = projectFeignClient.pageShortLink(
                linkPageReqDTO.getGid(),
                linkPageReqDTO.getCurrent(),
                linkPageReqDTO.getSize(),
                linkPageReqDTO.getOrderFlag()
        );
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        Result<Void> result = projectFeignClient.updateShortLink(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
    }

    @Override
    public void removeShortLink(RecycleDTO recycleDTO) {
        Result<Void> result = projectFeignClient.removeShortLink(recycleDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
    }


}
