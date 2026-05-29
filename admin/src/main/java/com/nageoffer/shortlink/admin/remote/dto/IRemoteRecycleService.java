package com.nageoffer.shortlink.admin.remote.dto;

import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkRecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;

import java.util.List;

public interface IRemoteRecycleService {

    void saveRecycleBin(ShortLinkRecycleDTO recycleDTO);

    List<ShortLinkRespDTO> pageRecycle(RecyclePageDTO pageReqDTO);
}
