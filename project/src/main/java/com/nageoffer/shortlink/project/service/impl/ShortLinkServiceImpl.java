package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.framework.exception.ClientException;
import com.nageoffer.shortlink.framework.exception.ServiceException;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.dao.entity.LinkStatsTodayDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGoDO;
import com.nageoffer.shortlink.project.dao.mapper.LinkStatsTodayMapper;
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
import com.nageoffer.shortlink.project.service.FaviconService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    private final FaviconService faviconService;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final TransactionTemplate transactionTemplate;

    private static final long STATS_SET_TTL_DAYS = 2L;

    @Value("${spring.short-link.block-domain-list}")
    private String blockDomainList;

    @Value("${spring.short-link.domain}")
    private String domain;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLink(ShortLinkReqDTO reqDTO) {
        String originUrl = reqDTO.getOriginUrl();

        boolean blocked = Arrays.stream(blockDomainList.split(","))
                .anyMatch(keyword -> originUrl.contains(keyword.trim()));
        if (blocked) {
            throw new ClientException("该域名不允许创建短链接");
        }
        String shortLink = HashUtil.createBase62Link(originUrl);
        String fullShortUrl = domain + "/" + shortLink;
        //布隆过滤器
        fullShortUrl = judgeHadShortUrl(fullShortUrl, reqDTO.getOriginUrl(), domain);
        shortLink = fullShortUrl.substring(fullShortUrl.lastIndexOf("/") + 1);
        ShortLinkDO shortLinkDO = BeanUtil
                .copyProperties(reqDTO, ShortLinkDO.class)
                .setFullShortUrl(fullShortUrl)
                .setShortUri(shortLink)
                .setUserName(UserContext.getUserName())
                .setFavicon(getDefaultFavicon(reqDTO.getOriginUrl()))
                .setTotalPv(0).setTotalUip(0).setTotalUv(0);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            log.warn("短链接生成重复:{}，gid:{}", fullShortUrl, reqDTO.getGid());
            fullShortUrl = forceRegenerate(reqDTO.getOriginUrl(), domain);
            shortLink = fullShortUrl.substring(fullShortUrl.lastIndexOf("/") + 1);
            shortLinkDO.setFullShortUrl(fullShortUrl)
                    .setShortUri(shortLink);
            try {
                baseMapper.insert(shortLinkDO);
            } catch (DuplicateKeyException ex) {
                log.error("短链接生成重复:{}，gid:{}", fullShortUrl, reqDTO.getGid());
                throw new ServiceException("短链接生成出错");
            }
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
        faviconService.updateFavicon(fullShortUrl, reqDTO.getGid(), originUrl);
        return new ShortLinkCreateRespDTO()
                .setFullShortUrl(fullShortUrl)
                .setGid(shortLinkDO.getGid())
                .setOriginUrl(shortLinkDO.getOriginUrl())
                .setFavicon(getDefaultFavicon(originUrl))
                .setDescription(reqDTO.getDescription());
    }

    /**
     * 分布式锁创建短链接（无线程安全问题）
     *
     * @param reqDTO 请求参数
     * @return 创建短链接结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkReqDTO reqDTO) {
        String originUrl = reqDTO.getOriginUrl();
        String shortLink = HashUtil.createBase62Link(originUrl);
        String fullShortUrl = domain + "/" + shortLink;
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
                fullShortUrl = domain + "/" + shortLink;
                exist = shortLinkGoToMapper.selectOne(Wrappers.lambdaQuery(ShortLinkGoDO.class)
                        .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
                if (rebuildCount >= 10) {
                    throw new ClientException("短链接重复创建");
                }
            }
            ShortLinkDO shortLinkDO = BeanUtil
                    .copyProperties(reqDTO, ShortLinkDO.class)
                    .setFullShortUrl(fullShortUrl)
                    .setUserName(UserContext.getUserName())
                    .setShortUri(shortLink)
                    .setFavicon(getDefaultFavicon(reqDTO.getOriginUrl()))
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
            faviconService.updateFavicon(fullShortUrl, reqDTO.getGid(), originUrl);
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
        List<ShortLinkRespDTO> records = baseMapper.pageShortLinkWithStats(linkPage, linkPageReqDTO.getGid(), linkPageReqDTO.getOrderFlag(), UserContext.getUserName()).getRecords();
        if (records.isEmpty()) {
            return records;
        }
        Set<String> urls = records.stream().map(ShortLinkRespDTO::getFullShortUrl).collect(Collectors.toSet());
        List<LinkStatsTodayDO> todayStats = linkStatsTodayMapper.selectList(
                Wrappers.<LinkStatsTodayDO>lambdaQuery()
                        .in(LinkStatsTodayDO::getFullShortUrl, urls)
                        .eq(LinkStatsTodayDO::getDate, LocalDate.now())
                        .eq(LinkStatsTodayDO::getDelFlag, 0));
        Map<String, LinkStatsTodayDO> statsMap = todayStats.stream()
                .collect(Collectors.toMap(LinkStatsTodayDO::getFullShortUrl, s -> s, (a, b) -> a));
        for (ShortLinkRespDTO r : records) {
            LinkStatsTodayDO s = statsMap.get(r.getFullShortUrl());
            if (s != null) {
                r.setTodayPv(s.getTodayPv()).setTodayUv(s.getTodayUv()).setTodayIpCount(s.getTodayIpCount());
            }
        }
        return records;
    }

    @Override
    public void updateShortLink(ShortLinkUpReqDTO reqDTO) {
        ShortLinkDO shortLinkDO = lambdaQuery().eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl()).one();
        boolean ifGidDiff = !Objects.equals((shortLinkDO.getGid()), reqDTO.getGid());
        boolean ifOriUrlDiff = !Objects.equals((shortLinkDO.getOriginUrl()), reqDTO.getOriginUrl());
        //当修改 gid 时
        if (ifGidDiff) {
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, reqDTO.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            rLock.lock();
            try {
                transactionTemplate.execute(status -> {
                    // 查询放到锁内保证读到数据后其他线程插入访问，导致访问量增加使得数据迁移前后不一致
                    ShortLinkDO hasShortLinkDO = lambdaQuery()
                            .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                            .eq(ShortLinkDO::getDelFlag, 0)
                            .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                            .eq(ShortLinkDO::getGid, reqDTO.getGid())
                            .one();
                    if (Objects.isNull(hasShortLinkDO)) {
                        throw new ClientException("短连接不存在");
                    }
                    lambdaUpdate()
                            .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                            .eq(ShortLinkDO::getDelFlag, 0)
                            .set(ShortLinkDO::getDelFlag, 1)
                            .set(ShortLinkDO::getEnableStatus, 1)
                            .set(ShortLinkDO::getDelTime, System.currentTimeMillis())
                            .update();
                    ShortLinkDO newShortLinkDO = hasShortLinkDO
                            .setGid(reqDTO.getGid())
                            .setDescription(reqDTO.getDescription())
                            .setValidDateType(reqDTO.getValidDateType())
                            .setValidDate(reqDTO.getValidDateType() == 0
                                    ? null
                                    : reqDTO.getValidDate())
                            .setDelFlag(0)
                            .setDelTime("0")
                            .setEnableStatus(0);
                    baseMapper.insert(newShortLinkDO);
                    LambdaUpdateWrapper<ShortLinkGoDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkGoDO.class)
                            .eq(ShortLinkGoDO::getFullShortUrl, reqDTO.getFullShortUrl())
                            .set(ShortLinkGoDO::getGid, reqDTO.getGid());
                    shortLinkGoToMapper.update(null, updateWrapper);
                    if (ifOriUrlDiff) {
                        stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, reqDTO.getFullShortUrl()));
                    }
                    return null;
                });
            } finally {
                rLock.unlock();
            }
        }
        // 不修改 gid 时无需迁移数据
        if (!ifGidDiff) {
            ShortLinkDO hasShortLinkDO = lambdaQuery()
                    .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                    .eq(ShortLinkDO::getGid, reqDTO.getGid())
                    .one();
            if (Objects.isNull(hasShortLinkDO)) {
                throw new ClientException("短连接不存在");
            }
            lambdaUpdate()
                    .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .set(ShortLinkDO::getValidDateType, reqDTO.getValidDateType())
                    .set(ShortLinkDO::getDescription, reqDTO.getDescription())
                    .set(ShortLinkDO::getValidDate, reqDTO.getValidDateType() == 0
                            ? null
                            : reqDTO.getValidDate())
                    .update();
            if (ifOriUrlDiff) {
                stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, reqDTO.getFullShortUrl()));
            }
        }
    }

    @Override
    public void gotoOriginUrl(String shortLinkUri, ServletRequest request, ServletResponse response) {
        String fullShortUrl = domain + "/" + shortLinkUri;
        String originUrl = stringRedisTemplate.opsForValue()
                .get(String.format(FULL_SHORT_LINK, fullShortUrl));
        ShortLinkStatsRecordDTO srDTO = null;
        //有缓存
        if (!StrUtil.isBlank(originUrl)) {
            srDTO = addLinkStats(fullShortUrl, request, response);
            GotoUrl(originUrl, response);
            if (Objects.nonNull(srDTO)) {
                sendMsg(srDTO);
            }
            return;
        }
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
                srDTO = addLinkStats(fullShortUrl, request, response);
                GotoUrl(originUrl, response);
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
            srDTO = addLinkStats(fullShortUrl, request, response);
            GotoUrl(shortLinkDO.getOriginUrl(), response);

        } finally {
            rLock.unlock();
            if (Objects.nonNull(srDTO)) {
                sendMsg(srDTO);
            }
        }
    }

    public ShortLinkStatsRecordDTO addLinkStats(String fullShortUrl, ServletRequest request, ServletResponse response) {
        AtomicReference<String> uv = new AtomicReference<>();
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        AtomicBoolean uipFirstFlag = new AtomicBoolean();
        ShortLinkStatsRecordDTO statsRecord;
        try {
            LocalDate currentDate = LocalDate.now();
            String uvStatsKey = String.format(LINK_STATS_UV, fullShortUrl, currentDate);
            String uipStatsKey = String.format(LINK_STATS_UIP, fullShortUrl, currentDate);

            Runnable addCookie = () -> {
                uv.set(UUID.fastUUID().toString());
                Cookie uvCookie = new Cookie("uv", uv.get());
                uvCookie.setPath(fullShortUrl.substring(fullShortUrl.indexOf("/")));
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                ((HttpServletResponse) response).addCookie(uvCookie);
                stringRedisTemplate.opsForSet().add(uvStatsKey, uv.get());
                stringRedisTemplate.expire(uvStatsKey, STATS_SET_TTL_DAYS, TimeUnit.DAYS);
                uvFirstFlag.set(true);
            };

            Cookie[] cookies = ((HttpServletRequest) request).getCookies();
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each -> {
                            uv.set(each);
                            Long added = stringRedisTemplate.opsForSet().add(uvStatsKey, each);
                            stringRedisTemplate.expire(uvStatsKey, STATS_SET_TTL_DAYS, TimeUnit.DAYS);
                            uvFirstFlag.set(added != null && added > 0L);
                        }, addCookie);
            } else {
                addCookie.run();
            }

            String clientIp = LinkUtil.getClientIp((HttpServletRequest) request);
            Long addedUip = stringRedisTemplate.opsForSet().add(uipStatsKey, clientIp);
            stringRedisTemplate.expire(uipStatsKey, STATS_SET_TTL_DAYS, TimeUnit.DAYS);
            uipFirstFlag.set(addedUip != null && addedUip > 0L);

            String os = LinkUtil.getOs((HttpServletRequest) request);
            String browser = LinkUtil.getBrowser((HttpServletRequest) request);
            String device = LinkUtil.getDevice((HttpServletRequest) request);
            String network = LinkUtil.getNetwork((HttpServletRequest) request);

            statsRecord = new ShortLinkStatsRecordDTO()
                    .setFullShortUrl(fullShortUrl)
                    .setRemoteAddr(clientIp)
                    .setOs(os)
                    .setBrowser(browser)
                    .setDevice(device)
                    .setNetwork(network)
                    .setUv(uv.get())
                    .setUvFirstFlag(uvFirstFlag.get())
                    .setUipFirstFlag(uipFirstFlag.get())
                    .setCurrentDate(currentDate);
        } catch (Exception e) {
            log.error("短链接信息收集异常", e);
            return null;
        }
        return statsRecord;
    }

    public void sendMsg(ShortLinkStatsRecordDTO statsRecord) {
        linkStatsProducer.send(statsRecord);
    }

    @Override
    public void removeShortLink(RecycleDTO recycleDTO) {
        boolean removed = lambdaUpdate()
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .eq(ShortLinkDO::getGid, recycleDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, recycleDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getDelFlag, 1)
                .set(ShortLinkDO::getDelTime, System.currentTimeMillis())
                .update();
        if (!removed) {
            throw new ClientException("短链接删除失败");
        }
        stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, recycleDTO.getFullShortUrl()));
    }

    /**
     * 根据原链接跳转
     */
    private void GotoUrl(String originUrl, ServletResponse response) {
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
    private String getDefaultFavicon(String originUrl) {
        java.net.URI uri = java.net.URI.create(originUrl);
        return uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
    }


    public String judgeHadShortUrl(String fullShortUrl, String originUrl, String domain) {
        int count = 0;
        while (bloomFilter.contains(fullShortUrl)) {
            originUrl += UUID.randomUUID().toString();
            String shortLink = HashUtil.createBase62Link(originUrl);
            fullShortUrl = domain + "/" + shortLink;
            if (++count > 10) {
                throw new ClientException("重复创建");
            }
        }
        return fullShortUrl;
    }

    public String forceRegenerate(String originUrl, String domain) {
        int count = 0;
        String fullShortUrl;
        while (true) {
            originUrl += UUID.randomUUID().toString();
            String shortLink = HashUtil.createBase62Link(originUrl);
            fullShortUrl = domain + "/" + shortLink;
            if (!bloomFilter.contains(fullShortUrl)) {
                return fullShortUrl;
            }
            if (++count > 10) {
                throw new ClientException("重复创建");
            }
        }
    }

}
