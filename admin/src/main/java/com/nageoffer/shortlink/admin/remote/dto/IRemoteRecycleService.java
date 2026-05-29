package com.nageoffer.shortlink.admin.remote.dto;

import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;

import java.util.List;

public interface IRemoteRecycleService {

    /*
    移动至回收站
     */
    void saveRecycleBin(RecycleDTO recycleDTO);

    /*
    回收站分页查询
     */
    List<ShortLinkRespDTO> pageRecycle(RecyclePageDTO pageReqDTO);

    /*
    恢复短链接
     */
    void rmRecycleBin(RecycleDTO recycleDTO);
}
