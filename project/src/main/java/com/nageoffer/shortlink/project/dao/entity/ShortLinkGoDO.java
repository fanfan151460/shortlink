package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@TableName("t_link_goto")
@Data
@Accessors(chain = true)
public class ShortLinkGoDO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "gid")
    private String gid;

    @TableField(value = "full_short_url")
    private String fullShortUrl;
}
