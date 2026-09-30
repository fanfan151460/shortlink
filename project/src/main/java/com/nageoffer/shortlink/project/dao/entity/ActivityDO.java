package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 营销活动实体
 * <p>
 * 一个活动 = 一个目标链接 + N 个渠道短链（渠道短链落在 t_link，见 ShortLinkDO.activityId / channel）
 */
@TableName("t_activity")
@Data
@Accessors(chain = true)
public class ActivityDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动目标链接（各渠道短链共享）
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 创建用户
     */
    private String userName;

    /**
     * 活动状态 0：进行中 1：已结束（未设置时由 DDL 默认值 0 兜底）
     */
    private Integer status;

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
     * 删除标识 0：未删除 1：已删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

    /**
     * 删除时间戳，未删除时为字符串 "0"（配合唯一索引区分逻辑删除）
     */
    @TableField(fill = FieldFill.INSERT)
    private String delTime;
}
