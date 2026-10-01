package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * 按活动批量创建渠道短链接
 * <p>
 * 目标链接和分组取自活动本身，不由调用方传入，避免渠道短链与活动指向不一致
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
     * 有效期类型 0：永久有效 1：用户自定义。
     * <p>
     * 为空时继承活动自身的有效期（而不是默认永久）——自检页/curl/第三方调用方漏传时，
     * 拿到的是"跟随活动"这个安全默认值。
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
