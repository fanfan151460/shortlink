package com.nageoffer.shortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ActivityReqDTO {

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
     * 活动状态 0：进行中 1：已结束（未设置时由 DDL 默认值 0 兜底）
     */
    private Integer status;

    /**
     * 有效期类型 0：永久有效 1：用户自定义（未设置时按永久有效处理）
     */
    private Integer validDateType;

    /**
     * 有效期（仅自定义日期时有值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;

}
