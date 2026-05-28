package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShortLinkGoToReqDTO {

    private String gid;

    private String fullShortUrl;

}
