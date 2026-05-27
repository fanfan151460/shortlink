package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;


public interface IShortLinkService extends IService<ShortLinkDO> {
    /**
     * createShortLink
     * @param reqDTO 请求link参数
     * @return 返回类型link
     */
    ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO);
}
