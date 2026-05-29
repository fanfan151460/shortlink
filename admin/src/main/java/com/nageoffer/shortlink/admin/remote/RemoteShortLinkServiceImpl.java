package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteShortLinkService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RemoteShortLinkServiceImpl implements IRemoteShortLinkService {

    private static final String BASE_URL = "http://localhost:8001/api/short-link/v1";

    private final RestTemplate restTemplate;

    @Override
    public ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO) {
        String url = BASE_URL + "/create";
        Result<ShortLinkRespDTO> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(reqDTO),
                new ParameterizedTypeReference<Result<ShortLinkRespDTO>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程创建短链接失败");
        }
        return result.getData();
    }

}
