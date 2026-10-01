package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ActivityDO;
import com.nageoffer.shortlink.project.dto.req.ActivityLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ActivityRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

import java.util.List;

public interface IActivityService extends IService<ActivityDO> {
    /**
     * 创建推销活动
     * @param activity 活动请求实体
     */
    void addNewActivity(ActivityReqDTO activity);

    /**
     * 更新活动名称 / 状态。目标链接与有效期创建后不可改，入参里也没有这两个字段
     * @param reqDTO 活动ID + 待更新字段（null 或空串表示不改该字段）
     */
    void updateActivity(ActivityUpdateReqDTO reqDTO);

    /**
     * 逻辑删除活动。不连带处理渠道短链：它们仍能正常跳转，
     * 并因为列表 SQL 只 JOIN 未删除的活动，会自动回到"短链接"列表里继续可管理。
     * @param id 活动ID
     */
    void removeActivity(Long id);

    /**
     * 创建活动各渠道短链接
     * @param reqDTO 活动ID + 渠道列表
     * @return 各渠道创建出的短链接
     */
    List<ShortLinkCreateRespDTO> addActivityLinks(ActivityLinkCreateReqDTO reqDTO);

    /**
     * 分页查询当前用户的营销活动
     * @param reqDTO 分页查询条件
     * @return 活动列表
     */
    List<ActivityRespDTO> pageActivity(ActivityPageReqDTO reqDTO);
}
