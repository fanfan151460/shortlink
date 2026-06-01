package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@TableName("t_link")
@Data
@Accessors(chain = true)
public class ShortLinkDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)

    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识 0：未启用 1：已启用
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer enableStatus;

    /**
     * 创建类型 0：控制台 1：接口
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createdType;

    /**
     * 历史pv
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer totalPv;

    /**
     * 历史uv
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer totalUv;

    /**
     * 历史uip
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer totalUip;


    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 描述
     */
    private String description;

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
    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

    /**
     * 网站图标
     */
    private String favicon;


}
