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

    /**
     * 预览"整组移入回收站"会扫走多少条（只数 del_flag = 0 的）
     * <p>
     * 列表页默认只显示普通短链，但整组移入是无差别全扫，活动名下的渠道短链也会被带走。
     * 前端拿这个数在确认框里把真实范围写清楚，免得"界面上 7 条、实际移走 11 条"。
     * @param gid 分组标识
     */
    Long countRecycleBinAll(String gid);
}
