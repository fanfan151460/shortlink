package com.nageoffer.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteAccessLogsService;
import com.nageoffer.shortlink.admin.remote.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RemoteAccessLogsServiceImpl implements IRemoteAccessLogsService {

    private static final String BASE_URL = "http://localhost:8082/api/short-link/v1/access-logs";

    @Override
    public List<accessLogRespDTO> getAccessLogs(AccessLogReqDTO reqDTO) {
        Map<String, Object> params = new HashMap<>();
        params.put("current", reqDTO.getCurrent());
        params.put("size", reqDTO.getSize());
        params.put("fullShortUrl", reqDTO.getFullShortUrl());
        params.put("gid", reqDTO.getGid());
        params.put("startDate", reqDTO.getStartDate());
        params.put("endDate", reqDTO.getEndDate());
        String body = HttpUtil.get(BASE_URL, params);
        return JSONUtil.parseObj(body).getJSONArray("data").toList(accessLogRespDTO.class);
    }
}
