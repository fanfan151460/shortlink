package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更新营销活动
 * <p>
 * 刻意不包含 gid：gid 既是分片键又是权限维度，改了会让已生成的渠道短链跟活动分家
 * （渠道短链落在原 gid 的分片表里，活动却指向新分组）。
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
     * 活动目标链接。注意：只影响之后新建的渠道短链，
     * 已生成的渠道短链在创建时就复制走了目标链接，不会跟着改。
     */
    private String originUrl;

    /**
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;
}
