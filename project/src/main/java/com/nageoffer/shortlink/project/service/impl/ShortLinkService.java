package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

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
        //布隆过滤器
        while (bloomFilter.contains(shortLink)) {
            shortLink = HashUtil.createBase62Link(shortUri);
            count++;
            if (count > 10) {
                throw new ClientException("重复创建");
            }
        }
        String fullShortUrl = reqDTO.getDomain() + "/" + shortLink;
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
        bloomFilter.add(shortLink);
        return new ShortLinkRespDTO()
                .setFullShortUrl(fullShortUrl)
                .setGid(shortLinkDO.getGid())
                .setOriginUrl(shortLinkDO.getOriginUrl());
    }
}
