package com.nageoffer.shortlink.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ShortLinkRespDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 历史pv
     */
    private Integer totalPv;

    /**
     * 历史uv
     */
    private Integer totalUv;

    /**
     * 历史uip
     */
    private Integer totalUip;

    /**
     * 今日PV
     */
    private Integer todayPv;

    /**
     * 今日UV
     */
    private Integer todayUv;

    /**
     * 今日IP数
     */
    private Integer todayIpCount;

    /**
     * 描述
     */
    private String description;

    /**
     * 所属活动ID，null 表示普通短链
     */
    private Long activityId;

    /**
     * 所属活动名称，普通短链为 null
     */
    private String activityName;

    /**
     * 推广渠道标识，普通短链为 null
     */
    private String channel;

    /**
     * 启用标识 0：已启用 1：未启用
     */
    private Integer enableStatus;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期（仅自定义时有值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;
}
