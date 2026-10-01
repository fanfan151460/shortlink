package com.nageoffer.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 更新营销活动（不含 gid，理由见 project 侧同名 DTO）
 */
@Data
@Accessors(chain = true)
public class ActivityUpdateReqDTO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动目标链接，只影响之后新建的渠道短链
     */
    private String originUrl;

    /**
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;

    /**
     * 有效期类型 0：永久有效 1：用户自定义。为空表示不修改。
     */
    private Integer validDateType;

    /**
     * 有效期（仅自定义日期时有值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;
}
