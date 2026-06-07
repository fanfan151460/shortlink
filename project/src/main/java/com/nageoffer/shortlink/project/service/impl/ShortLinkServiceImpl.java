package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGoDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGoToMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.dto.req.LinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.mq.producer.LinkStatsProducer;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import com.nageoffer.shortlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements IShortLinkService {

    private final RBloomFilter<String> bloomFilter;
    private final ShortLinkGoToMapper shortLinkGoToMapper;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final LinkStatsProducer linkStatsProducer;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLink(ShortLinkReqDTO reqDTO) {

        int count = 0;
        String OriginUrl = reqDTO.getOriginUrl();
        String shortLink = HashUtil.createBase62Link(OriginUrl);
        String fullShortUrl = reqDTO.getDomain() + "/" + shortLink;

        //布隆过滤器
        while (bloomFilter.contains(fullShortUrl)) {
            OriginUrl += UUID.randomUUID().toString();
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
                .setFavicon(getFaviconUrl(reqDTO.getOriginUrl()))
                .setTotalPv(0)
                .setTotalUip(0)
                .setTotalUv(0);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (Exception e) {
            log.warn("短链接生成重复:{}", fullShortUrl);
            throw new ClientException("服务端出错");
        }
        shortLinkGoToMapper.insert(new ShortLinkGoDO()
                .setGid(reqDTO.getGid())
                .setFullShortUrl(fullShortUrl));
        bloomFilter.add(fullShortUrl);
        return new ShortLinkCreateRespDTO()
                .setFullShortUrl(fullShortUrl)
                .setGid(shortLinkDO.getGid())
                .setOriginUrl(shortLinkDO.getOriginUrl());
    }

    @Override
    public List<ShortLinkRespDTO> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        Page<ShortLinkRespDTO> linkPage = Page.of(linkPageReqDTO.getCurrent(), linkPageReqDTO.getSize());
        return baseMapper.pageShortLinkWithStats(linkPage, linkPageReqDTO.getGid(), linkPageReqDTO.getOrderFlag()).getRecords();
    }

    @Override
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, reqDTO.getFullShortUrl()));
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try {
            // TODO gid修改优化
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
            String fullShortUrl = lambdaQuery().eq(ShortLinkDO::getOriginUrl, reqDTO.getOriginUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0).one().getFullShortUrl();
            stringRedisTemplate.opsForValue()
                    .set(String.format(FULL_SHORT_LINK, fullShortUrl)
                            , reqDTO.getOriginUrl(), linkExpireTime, TimeUnit.MILLISECONDS);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void gotoOriginUrl(String shortLinkUri, ServletRequest request, ServletResponse response) {
        String domain = request.getServerName();
        String fullShortUrl = domain + "/" + shortLinkUri;
        String originUrl = stringRedisTemplate.opsForValue()
                .get(String.format(FULL_SHORT_LINK, fullShortUrl));
        //有缓存
        if (!StrUtil.isBlank(originUrl)) {
            addLinkStats(fullShortUrl, request, response);
            GotoUrl(originUrl, response, fullShortUrl);
            return;
        }
        /*
        1.分布式解决缓存击穿
        2.注意缓存穿透，查询数据库之前使用布隆过滤器判断，误判再查询数据库
         */
        RLock rLock = redissonClient.getLock(LOCK_SHORT_LINK);
        rLock.lock();
        try {
            //二次获取重建缓存,获取成功
            if (!StrUtil.isBlank(stringRedisTemplate.opsForValue()
                    .get(String.format(FULL_SHORT_LINK, fullShortUrl)))) {
                originUrl = stringRedisTemplate.opsForValue()
                        .get(String.format(FULL_SHORT_LINK, fullShortUrl));
                addLinkStats(fullShortUrl, request, response);
                GotoUrl(originUrl, response, fullShortUrl);
                return;
            }
            //未获取到缓存（缓存未重建）, 判断是否为恶意请求
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
                notFound(response);
                throw new ClientException("短链接不存在或已删除");
            }
            long linkExpireTime = LinkUtil.getLinkExpireTime(shortLinkDO.getValidDate());
            if (linkExpireTime < 0) {
                notFound(response);
                throw new ClientException("短链接已经过期");
            }
            //存入redis中，并设置有效期
            stringRedisTemplate.opsForValue()
                    .set(String.format(FULL_SHORT_LINK, fullShortUrl)
                            , shortLinkDO.getOriginUrl(), linkExpireTime, TimeUnit.MILLISECONDS);
            addLinkStats(fullShortUrl, request, response);
            GotoUrl(originUrl, response, fullShortUrl);
        } finally {
            rLock.unlock();
        }
    }

    public void addLinkStats(String fullShortUrl, ServletRequest request, ServletResponse response) {
        AtomicReference<String> uv = new AtomicReference<>();
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        AtomicBoolean uipFirstFlag = new AtomicBoolean();
        try {
            Runnable addCookie = () -> {
                uv.set(UUID.fastUUID().toString());
                Cookie uvCookie = new Cookie("uv", uv.get());
                uvCookie.setPath(fullShortUrl.substring(fullShortUrl.indexOf("/")));
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                ((HttpServletResponse) response).addCookie(uvCookie);
            };

            Cookie[] cookies = ((HttpServletRequest) request).getCookies();
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each -> {
                            uv.set(each);
                            Long added = stringRedisTemplate.opsForSet().add(LINK_STATS_UV + fullShortUrl, each);
                            stringRedisTemplate.expire(LINK_STATS_UV + fullShortUrl, 30, TimeUnit.DAYS);
                            uvFirstFlag.set(added != null && added > 0L);
                        }, addCookie);
            } else {
                new Thread(addCookie).start();
            }

            String clientIp = LinkUtil.getClientIp((HttpServletRequest) request);
            Long addedUip = stringRedisTemplate.opsForSet().add(LINK_STATS_UIP + fullShortUrl, clientIp);
            stringRedisTemplate.expire(LINK_STATS_UIP + fullShortUrl, 30, TimeUnit.DAYS);
            uipFirstFlag.set(addedUip != null && addedUip > 0L);

            String os = LinkUtil.getOs((HttpServletRequest) request);
            String browser = LinkUtil.getBrowser((HttpServletRequest) request);
            String device = LinkUtil.getDevice((HttpServletRequest) request);
            String network = LinkUtil.getNetwork((HttpServletRequest) request);

            ShortLinkStatsRecordDTO statsRecord = new ShortLinkStatsRecordDTO()
                    .setFullShortUrl(fullShortUrl)
                    .setRemoteAddr(clientIp)
                    .setOs(os)
                    .setBrowser(browser)
                    .setDevice(device)
                    .setNetwork(network)
                    .setUv(uv.get())
                    .setUvFirstFlag(uvFirstFlag.get())
                    .setUipFirstFlag(uipFirstFlag.get())
                    .setCurrentDate(LocalDate.now());

            sendMsg(statsRecord);
        } catch (Exception e) {
            log.error("短链接统计异常", e);
        }
    }

    public void sendMsg(ShortLinkStatsRecordDTO statsRecord) {
        HashMap<String, String> statsMap = new HashMap<>();
        statsMap.put("statsMap", JSONUtil.toJsonStr(statsRecord));
        linkStatsProducer.send(statsMap);
    }

    @Override
    public void removeShortLink(RecycleDTO recycleDTO) {
        boolean removed = lambdaUpdate()
                .eq(ShortLinkDO::getGid, recycleDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, recycleDTO.getFullShortUrl())
                .remove();
        if (!removed) {
            throw new ClientException("短链接删除失败");
        }
        stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, recycleDTO.getFullShortUrl()));
    }

    /**
     * 根据原链接跳转
     */
    private void GotoUrl(String originUrl, ServletResponse response, String fullShortUrl) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            ((HttpServletResponse) response).sendRedirect(originUrl);
        } catch (IOException e) {
            throw new ClientException("跳转失败");
        } finally {
            rLock.unlock();
        }
    }

    private void notFound(ServletResponse response) {
        try {
            ((HttpServletResponse) response).sendRedirect("/page/notFound");
        } catch (IOException e) {
            throw new ClientException("跳转notfound页面失败");
        }
    }

    /**
     * 获取图标
     */
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
