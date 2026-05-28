package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGoDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGoToMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.req.PageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkService extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements IShortLinkService {

    private final RBloomFilter<String> bloomFilter;
    private final ShortLinkGoToMapper shortLinkGoToMapper;

    @Override
    public ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO) {

        int count = 0;
        String OriginUrl = reqDTO.getOriginUrl();
        String shortLink = HashUtil.createBase62Link(OriginUrl);
        String fullShortUrl = reqDTO.getDomain() + "/" + shortLink;

        //布隆过滤器
        while (bloomFilter.contains(fullShortUrl)) {
            OriginUrl += System.currentTimeMillis();
            shortLink = HashUtil.createBase62Link(OriginUrl);
            fullShortUrl = reqDTO.getDomain() + "/" + shortLink;
            count++;
            if (count > 10) {
                throw new ClientException("重复创建");
            }
        }
        ShortLinkDO shortLinkDO = BeanUtil
                .copyProperties(reqDTO, ShortLinkDO.class)
                .setFullShortUrl(fullShortUrl)
                .setShortUri(shortLink);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (Exception e) {
            ShortLinkDO hasShortLink = lambdaQuery()
                    .eq(ShortLinkDO::getGid, reqDTO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .one();
            if (hasShortLink != null) {
                log.warn("短链接生成重复:{}", fullShortUrl);
                throw new ClientException("该短连接已经存在");
            }
            throw new RuntimeException(e);
        }
        shortLinkGoToMapper.insert(new ShortLinkGoDO()
                .setGid(reqDTO.getGid())
                .setFullShortUrl(fullShortUrl));
        bloomFilter.add(fullShortUrl);
        return new ShortLinkRespDTO()
                .setFullShortUrl(fullShortUrl)
                .setGid(shortLinkDO.getGid())
                .setOriginUrl(shortLinkDO.getOriginUrl());
    }

    @Override
    public List<ShortLinkRespDTO> pageShortLink(PageReqDTO pageReqDTO) {
        Page<ShortLinkDO> linkPage = Page.of(pageReqDTO.getCurrent(), pageReqDTO.getSize());
        //TODO 排序

        Wrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, pageReqDTO.getGid())
                .eq(ShortLinkDO::getDelFlag, 0);
        Page<ShortLinkDO> shortLinkDOPage = page(linkPage, wrapper);
        return shortLinkDOPage.getRecords()
                .stream().map(each -> BeanUtil.copyProperties(each, ShortLinkRespDTO.class))
                .toList();
    }

    @Override
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        lambdaUpdate()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .set(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .set(ShortLinkDO::getOriginUrl, reqDTO.getOriginUrl())
                .set(ShortLinkDO::getGid, reqDTO.getGid())
                .set(ShortLinkDO::getValidDateType, reqDTO.getValidDateType())
                .set(ShortLinkDO::getDescription, reqDTO.getDescription())
                .set(ShortLinkDO::getValidDate, reqDTO.getValidDateType() == 0
                        ? null
                        : reqDTO.getValidDate())
                .update();
    }

    @Override
    public void gotoUrl(String shortLinkUri, ServletRequest request, ServletResponse response) {
        String domain = request.getServerName();
        String fullShortUrl = domain + "/" + shortLinkUri;

        ShortLinkGoDO gotoDO = shortLinkGoToMapper.selectOne(
                Wrappers.lambdaQuery(ShortLinkGoDO.class)
                        .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
        if (gotoDO == null) {
            throw new ClientException("短链接不存在");
        }

        ShortLinkDO shortLinkDO = lambdaQuery()
                .eq(ShortLinkDO::getGid, gotoDO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .one();
        if (shortLinkDO == null) {
            throw new ClientException("短链接不存在或已删除");
        }

        try {
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
        } catch (IOException e) {
            throw new ClientException("跳转失败");
        }
    }
}
