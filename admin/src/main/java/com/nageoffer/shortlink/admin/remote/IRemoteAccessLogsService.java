package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.remote.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.accessLogRespDTO;

import java.util.List;

public interface IRemoteAccessLogsService {

    /**
     * 远程分页查询访问日志
     * @param accessLogReqDTO 请求参数
     * @return 返回
     */
    List<accessLogRespDTO> getAccessLogs(AccessLogReqDTO accessLogReqDTO);
}
