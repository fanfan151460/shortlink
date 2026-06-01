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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteShortLinkServiceImpl implements IRemoteShortLinkService {

    private static final String BASE_URL = "http://localhost:8082/api/short-link/v1";

    private final RestTemplate restTemplate;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkReqDTO reqDTO) {
        String url = BASE_URL + "/create";
        Result<ShortLinkCreateRespDTO> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(reqDTO),
                new ParameterizedTypeReference<Result<ShortLinkCreateRespDTO>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程创建短链接失败");
        }
        return result.getData();
    }

    @Override
    public List<ShortLinkRespDTO> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        String url = BASE_URL + "/page?current=" + linkPageReqDTO.getCurrent()
                + "&size=" + linkPageReqDTO.getSize()
                + "&gid=" + linkPageReqDTO.getGid()
                + "&orderFlag=" + linkPageReqDTO.getOrderFlag();
        Result<List<ShortLinkRespDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<List<ShortLinkRespDTO>>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程分页查询短链接失败");
        }
        return result.getData();
    }

    @Override
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        String url = BASE_URL + "/update";
        Result<Void> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(reqDTO),
                new ParameterizedTypeReference<Result<Void>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程更新短链接失败");
        }
    }

    @Override
    public void removeShortLink(RecycleDTO recycleDTO) {
        String url = BASE_URL + "/remove";
        Result<Void> result = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(recycleDTO),
                new ParameterizedTypeReference<Result<Void>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程删除短链接失败");
        }
    }
}
