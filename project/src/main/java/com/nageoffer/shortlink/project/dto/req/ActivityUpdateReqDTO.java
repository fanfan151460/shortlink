package com.nageoffer.shortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 更新营销活动
 * <p>
 * 刻意不包含 gid：gid 既是分片键又是权限维度，改了会让已生成的渠道短链跟活动分家
 * （渠道短链落在原 gid 的分片表里，活动却指向新分组）。
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
     * 活动目标链接。注意：只影响之后新建的渠道短链，
     * 已生成的渠道短链在创建时就复制走了目标链接，不会跟着改。
     */
    private String originUrl;

    /**
     * 活动状态 0：进行中 1：已结束
     */
    private Integer status;

    /**
     * 有效期类型 0：永久有效 1：用户自定义。为空表示不修改。
     * <p>
     * 注意：只影响之后新建的渠道短链，已生成的渠道短链在创建时就复制走了有效期，不会跟着改。
     */
    private Integer validDateType;

    /**
     * 有效期（仅自定义日期时有值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;
}
