package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteRecycleService;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
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
public class RemoteRecycleServiceImpl implements IRemoteRecycleService {

    private static final String BASE_URL = "http://localhost:8082/api/short-link/v1/recycle-bin";

    private final RestTemplate restTemplate;

    @Override
    public void saveRecycleBin(RecycleDTO recycleDTO) {
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

    @Override
    public List<ShortLinkRespDTO> pageRecycle(RecyclePageDTO pageReqDTO) {
        String url = BASE_URL + "/page?current=" + pageReqDTO.getCurrent()
                + "&size=" + pageReqDTO.getSize()
                + "&gid=" + pageReqDTO.getGid();
        Result<List<ShortLinkRespDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<List<ShortLinkRespDTO>>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程回收站分页查询失败");
        }
        return result.getData();
    }

    @Override
    public void rmRecycleBin(RecycleDTO recycleDTO) {
        String url = BASE_URL + "/recover";
        Result<Void> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(recycleDTO),
                new ParameterizedTypeReference<Result<Void>>() {}
        ).getBody();
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程恢复短链接失败");
        }
    }
}
