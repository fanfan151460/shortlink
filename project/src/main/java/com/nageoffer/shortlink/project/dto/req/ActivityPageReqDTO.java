package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 活动分页查询
 */
@Data
@Accessors(chain = true)
public class ActivityPageReqDTO {

    /**
     * 页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 分组标识，为空时查询当前用户全部分组
     */
    private String gid;

    /**
     * 活动状态 0：进行中 1：已结束，为空时不过滤
     */
    private Integer status;

    /**
     * 活动名称，模糊匹配，为空时不过滤
     */
    private String activityName;
}
