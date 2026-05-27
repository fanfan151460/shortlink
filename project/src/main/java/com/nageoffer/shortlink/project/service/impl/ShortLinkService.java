package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.req.PageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkService extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements IShortLinkService {

    private final RBloomFilter<String> bloomFilter;

    @Override
    public ShortLinkRespDTO createShortLink(ShortLinkReqDTO reqDTO) {

        int count = 0;
        String shortUri = reqDTO.getOriginUrl() + System.currentTimeMillis();
        String shortLink = HashUtil.createBase62Link(shortUri);
        String fullShortUrl = reqDTO.getDomain() + "/" + shortLink;

        //布隆过滤器
        while (bloomFilter.contains(fullShortUrl)) {
            shortLink = HashUtil.createBase62Link(shortUri);
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
            ShortLinkDO hasShortLink = lambdaQuery().eq(ShortLinkDO::getFullShortUrl, fullShortUrl).one();
            if (hasShortLink != null) {
                log.warn("短链接生成重复:{}", fullShortUrl);
                throw new ClientException("该短连接已经存在");
            }
            throw new RuntimeException(e);
        }
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
}
