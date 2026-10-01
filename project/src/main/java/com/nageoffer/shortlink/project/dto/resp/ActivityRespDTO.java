package com.nageoffer.shortlink.project.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 活动分页查询返回
 */
@Data
@Accessors(chain = true)
public class ActivityRespDTO {

    /**
     * 活动ID
     */
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
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期（仅自定义日期时有值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
