package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkRecycleDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;

import java.util.List;

public interface IRecycleBinService extends IService<ShortLinkDO> {
    void saveRecycleBin(ShortLinkRecycleDTO recycleDTO);

    List<ShortLinkRespDTO> pageRecycle(RecyclePageDTO pageReqDTO);
}
