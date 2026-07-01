package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
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
                // TODO 优化图标获取
                .setFavicon(getFaviconUrl(reqDTO.getOriginUrl()))
                .setTotalPv(0)
                .setTotalUip(0)
                .setTotalUv(0);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            log.warn("短链接生成重复:{}，gid:{}", fullShortUrl, reqDTO.getGid());
            throw new ClientException("服务端出错，请再试一次！");
        }
        shortLinkGoToMapper.insert(new ShortLinkGoDO()
                .setGid(reqDTO.getGid())
                .setFullShortUrl(fullShortUrl));

        //缓存预热
        stringRedisTemplate.opsForValue()
                .set(String.format(FULL_SHORT_LINK, fullShortUrl),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkExpireTime(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS);
        bloomFilter.add(fullShortUrl);
        return new ShortLinkCreateRespDTO()
                .setFullShortUrl(fullShortUrl)
                .setGid(shortLinkDO.getGid())
                .setOriginUrl(shortLinkDO.getOriginUrl());
    }

    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkReqDTO reqDTO) {
        String originUrl = reqDTO.getOriginUrl();
        String shortLink = HashUtil.createBase62Link(originUrl);
        String fullShortUrl = reqDTO.getDomain() + "/" + shortLink;
        int rebuildCount = 0;
        RLock lock = redissonClient.getLock("lock:create:");
        lock.lock();
        try {
            // 查DB判断是否已存在
            ShortLinkGoDO exist = shortLinkGoToMapper.selectOne(
                    Wrappers.lambdaQuery(ShortLinkGoDO.class)
                            .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
            while (exist != null) {
                rebuildCount++;
                shortLink = HashUtil.createBase62Link(originUrl + UUID.randomUUID());
                fullShortUrl = reqDTO.getDomain() + "/" + shortLink;
                exist = shortLinkGoToMapper.selectOne(Wrappers.lambdaQuery(ShortLinkGoDO.class)
                        .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
                if (rebuildCount >= 10) {
                    throw new ClientException("短链接重复创建");
                }
            }
            ShortLinkDO shortLinkDO = BeanUtil
                    .copyProperties(reqDTO, ShortLinkDO.class)
                    .setFullShortUrl(fullShortUrl)
                    .setShortUri(shortLink)
                    .setTotalPv(0).setTotalUip(0).setTotalUv(0);
            baseMapper.insert(shortLinkDO);
            shortLinkGoToMapper.insert(new ShortLinkGoDO()
                    .setGid(reqDTO.getGid()).setFullShortUrl(fullShortUrl));
            stringRedisTemplate.opsForValue()
                    .set(String.format(FULL_SHORT_LINK, fullShortUrl),
                            shortLinkDO.getOriginUrl(),
                            LinkUtil.getLinkExpireTime(shortLinkDO.getValidDate()),
                            TimeUnit.MILLISECONDS);
            bloomFilter.add(fullShortUrl);
            return new ShortLinkCreateRespDTO()
                    .setFullShortUrl(fullShortUrl)
                    .setGid(shortLinkDO.getGid())
                    .setOriginUrl(shortLinkDO.getOriginUrl());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ShortLinkRespDTO> pageShortLink(LinkPageReqDTO linkPageReqDTO) {
        Page<ShortLinkRespDTO> linkPage = Page.of(linkPageReqDTO.getCurrent(), linkPageReqDTO.getSize());
        return baseMapper.pageShortLinkWithStats(linkPage, linkPageReqDTO.getGid(), linkPageReqDTO.getOrderFlag()).getRecords();
    }

    @Override
    @Transactional
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        boolean ifGidDiff = !Objects.equals((lambdaQuery().eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl()).one().getGid()), reqDTO.getGid());
        boolean ifOriUrlDiff = !Objects.equals((lambdaQuery().eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl()).one().getOriginUrl()), reqDTO.getOriginUrl());
        ShortLinkDO hasShortLinkDO = lambdaQuery()
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .one();
        if (Objects.isNull(hasShortLinkDO)) {
            throw new ClientException("短连接不存在");
        }
        //当修改gid时
        if (ifGidDiff) {
            // TODO gid修改优化
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, reqDTO.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            rLock.lock();
            try {
                lambdaUpdate()
                        .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getEnableStatus, 0)
                        .set(ShortLinkDO::getDelFlag, 1)
                        .set(ShortLinkDO::getEnableStatus, 1)
                        // TODO del_time的修改
                        .update();
                LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                        .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                        .eq(ShortLinkDO::getDelFlag, 1)
                        .eq(ShortLinkDO::getEnableStatus, 1);
                ShortLinkDO oldShortLinkDO = baseMapper.selectOne(queryWrapper);
                ShortLinkDO newShortLinkDO = oldShortLinkDO
                        .setGid(reqDTO.getGid())
                        .setFullShortUrl(reqDTO.getFullShortUrl())
                        .setDescription(reqDTO.getDescription())
                        .setValidDateType(reqDTO.getValidDateType())
                        .setValidDate(reqDTO.getValidDateType() == 0
                                ? null
                                : reqDTO.getValidDate())
                        .setDelFlag(0)
                        // TODO del_time的修改
//                        .setDelTime(null)
                        .setEnableStatus(0);
                baseMapper.insert(newShortLinkDO);
                // TODO goto表的修改
//                shortLinkGoToMapper.update();
                if (!ifOriUrlDiff) {
                    stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, reqDTO.getFullShortUrl()));
                }
            } finally {
                rLock.unlock();
            }
        }
        if (!ifGidDiff) {
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
            if (!ifOriUrlDiff) {
                stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, reqDTO.getFullShortUrl()));
            }
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
        // 布隆过滤器前置，拦截穿透请求，避免恶意请求竞争锁
        if (!bloomFilter.contains(fullShortUrl)) {
            notFound(response);
            return;
        }
        // 防止布隆过滤器误判导致穿透，已确认不存在的URL快速失败
        if (!StrUtil.isBlank(stringRedisTemplate.opsForValue()
                .get(String.format(SHORT_URL_NULL_KEY, fullShortUrl)))) {
            notFound(response);
            return;
        }
        RLock rLock = redissonClient.getLock(String.format(LOCK_SHORT_LINK, fullShortUrl));
        rLock.lock();
        try {
            // 双检：真实缓存
            if (!StrUtil.isBlank(stringRedisTemplate.opsForValue()
                    .get(String.format(FULL_SHORT_LINK, fullShortUrl)))) {
                originUrl = stringRedisTemplate.opsForValue()
                        .get(String.format(FULL_SHORT_LINK, fullShortUrl));
                addLinkStats(fullShortUrl, request, response);
                GotoUrl(originUrl, response, fullShortUrl);
                return;
            }
            // 双检：空值缓存，防止布隆误判下多线程重复穿透到MySQL
            if (!StrUtil.isBlank(stringRedisTemplate.opsForValue()
                    .get(String.format(SHORT_URL_NULL_KEY, fullShortUrl)))) {
                notFound(response);
                return;
            }
            ShortLinkGoDO gotoDO = shortLinkGoToMapper.selectOne(
                    Wrappers.lambdaQuery(ShortLinkGoDO.class)
                            .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
            if (gotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(SHORT_URL_NULL_KEY, fullShortUrl), "1", 30, TimeUnit.MINUTES);
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
                stringRedisTemplate.opsForValue().set(String.format(SHORT_URL_NULL_KEY, fullShortUrl), "1", 30, TimeUnit.MINUTES);
                notFound(response);
                return;
            }
            long linkExpireTime = LinkUtil.getLinkExpireTime(shortLinkDO.getValidDate());
            if (linkExpireTime < 0) {
                stringRedisTemplate.opsForValue().set(String.format(SHORT_URL_NULL_KEY, fullShortUrl), "1", 1, TimeUnit.MINUTES);
                notFound(response);
                throw new ClientException("短链接已经过期");
            }
            //存入redis中，并设置有效期
            stringRedisTemplate.opsForValue()
                    .set(String.format(FULL_SHORT_LINK, fullShortUrl)
                            , shortLinkDO.getOriginUrl(), linkExpireTime, TimeUnit.MILLISECONDS);
            addLinkStats(fullShortUrl, request, response);
            GotoUrl(shortLinkDO.getOriginUrl(), response, fullShortUrl);
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
                            uvFirstFlag.set(added != null && added > 0L);
                        }, addCookie);
            } else {
                new Thread(addCookie).start();
            }

            String clientIp = LinkUtil.getClientIp((HttpServletRequest) request);
            Long addedUip = stringRedisTemplate.opsForSet().add(LINK_STATS_UIP + fullShortUrl, clientIp);
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
        linkStatsProducer.send(statsRecord);
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
        try {
            ((HttpServletResponse) response).sendRedirect(originUrl);
        } catch (IOException e) {
            throw new ClientException("跳转失败");
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
