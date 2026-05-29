package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
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
import com.nageoffer.shortlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.FULL_SHORT_LINK;
import static com.nageoffer.shortlink.project.common.constant.RedisConstant.LOCK_SHORT_LINK;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements IShortLinkService {

    private final RBloomFilter<String> bloomFilter;
    private final ShortLinkGoToMapper shortLinkGoToMapper;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

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
                .setShortUri(shortLink)
                .setFavicon(getFaviconUrl(reqDTO.getOriginUrl()));
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
                .eq(ShortLinkDO::getOriginUrl, reqDTO.getOriginUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .set(ShortLinkDO::getValidDateType, reqDTO.getValidDateType())
                .set(ShortLinkDO::getDescription, reqDTO.getDescription())
                .set(ShortLinkDO::getValidDate, reqDTO.getValidDateType() == 0
                        ? null
                        : reqDTO.getValidDate())
                .update();
        long linkExpireTime = LinkUtil.getLinkExpireTime(reqDTO.getValidDate());
        String fullShortUrl  = lambdaQuery().eq(ShortLinkDO::getOriginUrl, reqDTO.getOriginUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0).one().getFullShortUrl();
        stringRedisTemplate.opsForValue()
                .set(String.format(FULL_SHORT_LINK, fullShortUrl)
                        ,reqDTO.getOriginUrl(), linkExpireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void gotoOriginUrl(String shortLinkUri, ServletRequest request, ServletResponse response) {
        String domain = request.getServerName();
        String fullShortUrl = domain + "/" + shortLinkUri;
        String originUrl = stringRedisTemplate.opsForValue()
                .get(String.format(FULL_SHORT_LINK, fullShortUrl));
        //有缓存
        if (!StrUtil.isBlank(originUrl)) {
            GotoUrl(originUrl, response);
            return;
        }
        /*
        1.分布式解决缓存重建
        2.注意缓存穿透，查询数据库之前使用布隆过滤器判断，误判再查询数据库
         */
        RLock rLock = redissonClient.getLock(LOCK_SHORT_LINK);
        rLock.lock();
        try {
            //二次获取重建缓存,获取成功
            if (!StrUtil.isBlank(stringRedisTemplate.opsForValue()
                    .get(String.format(FULL_SHORT_LINK, fullShortUrl))))
            {
                originUrl = stringRedisTemplate.opsForValue()
                        .get(String.format(FULL_SHORT_LINK, fullShortUrl));
                GotoUrl(originUrl, response);
                notFound(response);
                return;
            }
            //未获取到缓存（缓存未重建）
            if (!bloomFilter.contains(fullShortUrl)) {
                notFound(response);
                return;
            }
            ShortLinkGoDO gotoDO = shortLinkGoToMapper.selectOne(
                    Wrappers.lambdaQuery(ShortLinkGoDO.class)
                            .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
            if (gotoDO == null) {
                notFound(response);
                return;
            }

            ShortLinkDO shortLinkDO = lambdaQuery()
                    .eq(ShortLinkDO::getGid, gotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .one();
            if (shortLinkDO == null) {
                throw new ClientException("短链接不存在或已删除");
            }
            long linkExpireTime = LinkUtil.getLinkExpireTime(shortLinkDO.getValidDate());
            if(linkExpireTime < 0){
                throw new ClientException("短链接已经过期");
            }
            stringRedisTemplate.opsForValue()
                    .set(String.format(FULL_SHORT_LINK, fullShortUrl)
                            ,shortLinkDO.getOriginUrl(), linkExpireTime, TimeUnit.MILLISECONDS);
            GotoUrl(originUrl, response);
        } finally {
            rLock.unlock();
        }
    }

    //跳转到原始链接
    public void GotoUrl(String originUrl, ServletResponse response) {
        try {
            ((HttpServletResponse) response).sendRedirect(originUrl);
        } catch (IOException e) {
            throw new ClientException("跳转失败");
        }
    }

    public void notFound(ServletResponse response) {
        try {
            ((HttpServletResponse) response).sendRedirect("/page/notFound");
        } catch (IOException e) {
            throw new ClientException("跳转notfound页面失败");
        }
    }

    private String getFaviconUrl(String originUrl) {
        try {
            String html = HttpUtil.get(originUrl);
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("<link[^>]+rel=[\"']?(?:shortcut )?icon[\"']?[^>]+href=[\"']([^\"']+)[\"']",
                            java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(html);
            if (matcher.find()) {
                String href = matcher.group(1);
                if (href.startsWith("http") || href.startsWith("//")) {
                    return href.startsWith("//") ? "https:" + href : href;
                }
                java.net.URI uri = java.net.URI.create(originUrl);
                return uri.getScheme() + "://" + uri.getHost() + (href.startsWith("/") ? "" : "/") + href;
            }
        } catch (Exception ignored) {
        }
        java.net.URI uri = java.net.URI.create(originUrl);
        return uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
    }

}
