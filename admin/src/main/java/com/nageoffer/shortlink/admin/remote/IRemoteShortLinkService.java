package com.nageoffer.shortlink.admin.remote;

import com.nageoffer.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;

import java.util.List;

public interface IRemoteShortLinkService {

    /**
     * 远程创建短链接
     * @param reqDTO 请求参数
     * @return 返回
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkReqDTO reqDTO);

    /**
     * 远程分页查询
     * @param linkPageReqDTO 请求参数
     * @return 返回
     */
    List<ShortLinkRespDTO> pageShortLink(LinkPageReqDTO linkPageReqDTO);

    /**
     * 远程更新短链接
     * @param reqDTO 请求参数
     */
    void updateShortLink(ShortLinkUpReqDTO reqDTO);

    /**
     * 远程删除短链接
     * @param recycleDTO 请求参数
     */
    void removeShortLink(RecycleDTO recycleDTO);

}
