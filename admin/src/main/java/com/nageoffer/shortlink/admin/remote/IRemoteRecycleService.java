package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.RecycleBinShortLinkDTO;

import java.util.List;

public interface IRemoteRecycleService {

    /*
    移动至回收站
     */
    void saveRecycleBin(RecycleDTO recycleDTO);

    /*
    回收站分页查询
     */
    List<RecycleBinShortLinkDTO> pageRecycle(RecyclePageDTO pageReqDTO);

    /*
    恢复短链接
     */
    void rmRecycleBin(RecycleDTO recycleDTO);

    /**
     * 将分组下所有短链接放入回收站
     * @param gid
     */
    void saveRecycleBinAll(String gid);
}
