package com.nageoffer.shortlink.project.dto.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 活动分页查询返回
 */
@Data
@Accessors(chain = true)
public class ActivityRespDTO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动目标链接（各渠道短链共享）
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 创建用户
     */
    private String userName;

    /**
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
