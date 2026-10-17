package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 短链接分页查询
 */
@Data
@Accessors(chain = true)
public class LinkPageReqDTO {

    /**
     * 页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * gid
     */
    private String gid;

    /**
     * 排序字段
     */
    private String orderFlag;

    /**
     * 活动ID，非空时只查询该活动的渠道短链
     */
    private Long activityId;

    /**
     * 是否把活动名下的渠道短链也查出来。为空/false 时列表只有普通短链。
     * <p>
     * 判定用的是 activity_id 本身，不是"活动还在不在"——活动被逻辑删除后链接仍然带着
     * activity_id，按活动存活与否来决定可见性会让链接"突然冒出来"，用户无法预期。
     */
    private Boolean includeActivity;
}
