package com.nageoffer.shortlink.admin.common.biz.user;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.framework.result.Result;

public class SentinelBlockUtil {

    public static Result<ShortLinkRespDTO> createShortLinkBlock(ShortLinkReqDTO reqDTO, BlockException e) {
        return new Result<ShortLinkRespDTO>().setCode("000001").setMessage("当前访问人数过多");
    }
}
