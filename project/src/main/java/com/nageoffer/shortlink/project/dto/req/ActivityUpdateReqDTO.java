package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更新营销活动
 * <p>
 * 只开放「名称 + 状态」两个字段。目标链接和有效期在创建时定死：
 * 渠道短链在创建那一刻就把活动当时的目标链接和有效期复制走了，
 * 事后改活动不回溯已有渠道，改了只会让"活动上写的"和"渠道实际的"对不上。
 * <p>
 * 所以这里直接不暴露这两个字段，而不是"传了也忽略"——不给入口比给了再悄悄丢掉更不容易误用。
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
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;
}
