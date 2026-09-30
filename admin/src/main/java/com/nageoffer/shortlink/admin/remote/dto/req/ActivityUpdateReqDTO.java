package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更新营销活动（不含 gid，理由见 project 侧同名 DTO）
 */
@Data
@Accessors(chain = true)
public class ActivityUpdateReqDTO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动目标链接，只影响之后新建的渠道短链
     */
    private String originUrl;

    /**
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;
}
