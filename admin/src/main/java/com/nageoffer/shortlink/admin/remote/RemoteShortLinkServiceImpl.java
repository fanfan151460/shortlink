package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteShortLinkService;
import com.nageoffer.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteShortLinkServiceImpl implements IRemoteShortLinkService {

    private final ProjectFeignClient projectFeignClient;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkReqDTO reqDTO) {
        System.out.println(">>> Feign calling: createShortLink, gid=" + reqDTO.getGid());
        Result<ShortLinkCreateRespDTO> result = projectFeignClient.createShortLink(reqDTO);
        System.out.println(">>> Feign returned: " + (result == null ? "null" : result.getCode()));
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程创建短链接失败");
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
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程分页查询短链接失败");
        }
        return result.getData();
    }

    @Override
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        Result<Void> result = projectFeignClient.updateShortLink(reqDTO);
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程更新短链接失败");
        }
    }

    @Override
    public void removeShortLink(RecycleDTO recycleDTO) {
        Result<Void> result = projectFeignClient.removeShortLink(recycleDTO);
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程删除短链接失败");
        }
    }

    @Override
    public String testProjectShortLink() {
        return projectFeignClient.testProject();
    }


}
