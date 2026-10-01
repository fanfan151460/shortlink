package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 批量停用 / 启用短链接（字段与 project 侧同名 DTO 手工同步）
 * <p>
 * fullShortUrls 或 activityId 二选一，同时传以 activityId 为准；gid 必传（分片键）。
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
