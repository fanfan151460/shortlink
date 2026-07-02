package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.IRemoteAccessLogsService;
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
        Result<List<accessLogRespDTO>> result = projectFeignClient.getAccessLogs(
                reqDTO.getFullShortUrl(),
                reqDTO.getGid(),
                reqDTO.getCurrent(),
                reqDTO.getSize(),
                reqDTO.getStartDate(),
                reqDTO.getEndDate()
        );
        if (result == null || !result.isSuccess()) {
            throw new RuntimeException("远程查询访问日志失败");
        }
        return result.getData();
    }
}
