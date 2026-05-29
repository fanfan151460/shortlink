package com.nageoffer.shortlink.admin.remote.dto;

import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;

public interface IRemoteShortLinkService {

    ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO);

}
