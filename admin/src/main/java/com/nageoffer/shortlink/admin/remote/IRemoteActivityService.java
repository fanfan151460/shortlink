package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.remote.dto.req.ActivityLinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ActivityUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ActivityRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;

import java.util.List;

public interface IRemoteActivityService {

    /**
     * 远程创建营销活动
     * @param reqDTO 请求参数
     */
    void addNewActivity(ActivityReqDTO reqDTO);

    /**
     * 远程更新活动名称 / 目标链接 / 状态
     * @param reqDTO 请求参数
     */
    void updateActivity(ActivityUpdateReqDTO reqDTO);

    /**
     * 远程逻辑删除活动。归属校验在 project 侧按 username 完成，这里不做前置校验
     * @param id 活动ID
     */
    void removeActivity(Long id);

    /**
     * 远程分页查询活动
     * @param reqDTO 请求参数
     * @return 活动列表
     */
    List<ActivityRespDTO> pageActivity(ActivityPageReqDTO reqDTO);

    /**
     * 远程批量创建活动渠道短链
     * @param reqDTO 请求参数
     * @return 各渠道创建出的短链接
     */
    List<ShortLinkCreateRespDTO> addActivityLinks(ActivityLinkCreateReqDTO reqDTO);
}
