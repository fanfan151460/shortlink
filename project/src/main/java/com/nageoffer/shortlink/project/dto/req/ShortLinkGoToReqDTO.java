package com.nageoffer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShortLinkGoToReqDTO {

    private String gid;

    private String fullShortUrl;

}
