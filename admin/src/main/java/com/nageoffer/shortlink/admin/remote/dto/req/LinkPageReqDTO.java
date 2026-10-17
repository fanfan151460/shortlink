package com.nageoffer.shortlink.admin.remote.dto.req;

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
     * 是否把活动名下的渠道短链也查出来。为空/false 时列表只有普通短链
     */
    private Boolean includeActivity;
}
