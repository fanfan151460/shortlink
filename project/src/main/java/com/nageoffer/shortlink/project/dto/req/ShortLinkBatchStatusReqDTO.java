package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 批量停用 / 启用短链接
 * <p>
 * 两种入参二选一，同时传时以 activityId 为准：
 * <ul>
 *     <li>按列表：fullShortUrls，用于短链接列表页勾选/单条操作；</li>
 *     <li>按活动：activityId，用于活动页的"全部停用/全部启用"——
 *         渠道列表是分页的，前端只有当前页，传列表会漏。</li>
 * </ul>
 * 两种都必须带 gid：它是 t_link 的分片键，不带会被广播到 16 张分片表。
 */
@Data
@Accessors(chain = true)
public class ShortLinkBatchStatusReqDTO {

    /**
     * 分组标识（分片键，必传）
     */
    private String gid;

    /**
     * 完整短链接列表
     */
    private List<String> fullShortUrls;

    /**
     * 活动ID，按活动批量操作时使用
     */
    private Long activityId;
}
