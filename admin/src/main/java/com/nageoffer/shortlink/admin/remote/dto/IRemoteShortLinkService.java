package com.nageoffer.shortlink.admin.remote.dto;

import com.nageoffer.shortlink.admin.remote.dto.req.PageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;

import java.util.List;

public interface IRemoteShortLinkService {

    ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO);

    List<ShortLinkRespDTO> pageShortLink(PageReqDTO pageReqDTO);
}
