package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.project.dto.resp.RecycleBinShortLinkDTO;

import java.util.List;

public interface IRecycleBinService extends IService<ShortLinkDO> {
    /**
     * 放入回收站
     * @param recycleDTO 请求参数
     */
    void saveRecycleBin(RecycleDTO recycleDTO);

    /**
     * 分页查询
     * @param pageReqDTO 请求参数
     * @return 查询结果对象
     */
    List<RecycleBinShortLinkDTO> pageRecycle(RecyclePageDTO pageReqDTO);

    /**
     * 恢复短链接
     * @param recycleDTO 请求参数
     */
    void rmRecycleBin(RecycleDTO recycleDTO);

    /**
     * 删除短链接
     * @param recycleDTO 请求参数
     */
    void removeShortLink(RecycleDTO recycleDTO);

    /**
     * 将分组下所有短链接移入回收站
     * @param gid 分组标识
     * @return true=分组下有短链接（已移入回收站），false=空分组可直接物理删除
     */
    boolean saveRecycleBinAll(String gid);
}
