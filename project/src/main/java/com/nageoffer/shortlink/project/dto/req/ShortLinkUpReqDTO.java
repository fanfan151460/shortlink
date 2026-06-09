package com.nageoffer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ShortLinkUpReqDTO implements Serializable {

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 完成短链接
     */

    private String fullShortUrl;
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

}
