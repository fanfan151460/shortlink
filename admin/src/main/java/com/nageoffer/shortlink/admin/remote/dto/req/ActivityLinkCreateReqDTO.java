package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * 按活动批量创建渠道短链接
 */
@Data
@Accessors(chain = true)
public class ActivityLinkCreateReqDTO {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 推广渠道标识列表，空白项与重复项会被过滤
     */
    private List<String> channels;

    /**
     * 有效期类型 0：永久有效 1：用户自定义，为空时按永久有效处理
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private LocalDate validDate;

    /**
     * 描述
     */
    private String description;
}
