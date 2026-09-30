package com.nageoffer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ShortLinkReqDTO implements Serializable {

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private LocalDate validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String description;

    /**
     * 所属活动 ID，null 表示普通短链
     */
    private Long activityId;

    /**
     * 推广渠道标识，仅活动渠道短链有值（weixin / douyin / sms ...）
     */
    private String channel;
}
