package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 活动级跨渠道去重 UV 统计
 * <p>
 * 与 t_link_access_stats 的区别：那张表按 full_short_url 去重（每个渠道各算一次），
 * 这张表按 activity_id 去重（同一访客当天访问同一活动的多个渠道只算一次）。
 */
@Data
@Accessors(chain = true)
@TableName("t_activity_stats")
public class ActivityStatsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 当天活动去重UV（本次消费是否 +1 由 Redis Set 的 SADD 结果决定）
     */
    private Integer uv;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识：0 未删除 1 已删除
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}
