package com.nageoffer.shortlink.admin.remote.impl;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.ProjectFeignClient;
import com.nageoffer.shortlink.admin.remote.IRemoteAccessLogsService;
import com.nageoffer.shortlink.admin.remote.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteAccessLogsServiceImpl implements IRemoteAccessLogsService {

    private final ProjectFeignClient projectFeignClient;

    @Override
    public List<accessLogRespDTO> getAccessLogs(AccessLogReqDTO reqDTO) {
        Result<List<accessLogRespDTO>> result = projectFeignClient.getAccessLogs(reqDTO);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
        return result.getData();
    }
}
