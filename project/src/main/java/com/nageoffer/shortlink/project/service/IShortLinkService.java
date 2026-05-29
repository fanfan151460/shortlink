package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.req.PageReqDTO;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.util.List;


public interface IShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     * @param reqDTO 请求link参数
     * @return 返回类型link
     */
    ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO);

    /**
     * 分页查询短链接
     * @param pageReqDTO 请求参数
     * @return 返回类型link
     */
    List<ShortLinkRespDTO> pageShortLink(PageReqDTO pageReqDTO);

    /**
     * 更新短链接
     *
     * @param reqDTO 请求参数
     */
    void updateShortLink(ShortLinkUpReqDTO reqDTO);

    /**
     * 跳转原链接
     * @param shortLinkUri uri
     */
    void gotoOriginUrl(String shortLinkUri, ServletRequest request, ServletResponse response);

    /**
     * 删除短链接
     * @param recycleDTO 请求参数
     */
    void removeShortLink(RecycleDTO recycleDTO);
}
