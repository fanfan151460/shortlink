package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkRecycleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RemoteRecycleServiceImpl implements IRemoteRecycleService {

    private static final String BASE_URL = "http://localhost:8001/api/short-link/v1/recycle-bin";

    private final RestTemplate restTemplate;

    @Override
    public void saveRecycleBin(ShortLinkRecycleDTO recycleDTO) {
        String url = BASE_URL + "/save";
        Result<Void> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(recycleDTO),
                new ParameterizedTypeReference<Result<Void>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程移入回收站失败");
        }
    }
}
