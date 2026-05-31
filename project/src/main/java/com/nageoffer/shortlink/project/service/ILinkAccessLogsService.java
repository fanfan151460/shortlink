package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.nageoffer.shortlink.project.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.project.dto.resp.accessLogRespDTO;

import java.util.List;

public interface ILinkAccessLogsService extends IService<LinkAccessLogsDO> {
    /**
     * 分页查询日志
     * @return 结果
     */
    List<accessLogRespDTO> getAccessLogs(AccessLogReqDTO accessLogReqDTO);
}
