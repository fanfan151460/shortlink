package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ActivityReqDTO {

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
     * 活动状态 0：进行中 1：已结束（未设置时由 DDL 默认值 0 兜底）
     */
    private Integer status;

}
