package com.nageoffer.shortlink.project.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.dao.entity.*;
import com.nageoffer.shortlink.project.dao.mapper.*;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.mq.idempotent.MsgQueueIdempotentHandler;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import com.nageoffer.shortlink.project.util.LinkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.LOCK_GID_UPDATE_KEY;
import static com.nageoffer.shortlink.project.config.RocketMQConfig.LINK_STATS_CONSUMER_GROUP;
import static com.nageoffer.shortlink.project.config.RocketMQConfig.LINK_STATS_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(topic = LINK_STATS_TOPIC, consumerGroup = LINK_STATS_CONSUMER_GROUP)
public class LinkStatsConsumer implements RocketMQListener<Map<String, String>> {

    private final ShortLinkGoToMapper shortLinkGoToMapper;
    private final ShortLinkStatsMapper linkStatsMapper;
    private final LinkLocalStatsMapper linkLocalStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final RedissonClient redissonClient;
    private final IShortLinkService shortLinkService;
    private final MsgQueueIdempotentHandler idempotentHandler;

    @Value("${locale.gaoDe.apiKey}")
    private String apikey;

    @Override
    public void onMessage(Map<String, String> productMap) {
        String msgKey = productMap.get("keys");
        if (idempotentHandler.hasConsume(msgKey)) {
            if (idempotentHandler.isSuccessConsume(msgKey)) {
                return;
            }
            throw new ServiceException("消息消费失败，消息队列重试");
        }
        try {
        ShortLinkStatsRecordDTO shortLinkStatsRecordDTO = JSONUtil
                .toBean(productMap.get("productMap"), ShortLinkStatsRecordDTO.class);
            consume(shortLinkStatsRecordDTO);
            idempotentHandler.delConsume(msgKey);
        } catch (Throwable e) {
            idempotentHandler.delConsume(msgKey);
            log.error("短链接监控消费者异常", e);
            throw e;
        }
        idempotentHandler.successConsume(msgKey);
    }

    public void consume(ShortLinkStatsRecordDTO dto) {

        // 数据解析
        String fullShortUrl = dto.getFullShortUrl();
        String clientIp = dto.getRemoteAddr();
        String uv = dto.getUv();
        boolean uvFirstFlag = Boolean.TRUE.equals(dto.getUvFirstFlag());
        boolean uipFirstFlag = Boolean.TRUE.equals(dto.getUipFirstFlag());
        String os = dto.getOs();
        String browser = dto.getBrowser();
        String device = dto.getDevice();
        String network = dto.getNetwork();
        LocalDate today = dto.getCurrentDate() != null ? dto.getCurrentDate() : LocalDate.now();

        // 补全gid
        ShortLinkGoDO gotoDO = shortLinkGoToMapper.selectOne(
                Wrappers.lambdaQuery(ShortLinkGoDO.class)
                        .eq(ShortLinkGoDO::getFullShortUrl, fullShortUrl));
        if (gotoDO == null) {
            return;
        }
        String gid = gotoDO.getGid();

        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            // pv uv uip
            LinkStatsDO statsDO = new LinkStatsDO()
                    .setGid(gid)
                    .setFullShortUrl(fullShortUrl)
                    .setUv(uvFirstFlag ? 1 : 0)
                    .setUip(uipFirstFlag ? 1 : 0)
                    .setDate(today)
                    .setHour(LocalTime.now().getHour())
                    .setWeekday(today.getDayOfWeek().getValue());
            linkStatsMapper.insertLinkStats(statsDO);

            // 地区统计
            String localByIp = LinkUtil.getLocalByIp(apikey, clientIp);
            LinkLocalStatsDO linkLocalStatsDO = JSONUtil.toBean(localByIp, LinkLocalStatsDO.class);
            String province = linkLocalStatsDO.getProvince();
            if (StrUtil.equals(linkLocalStatsDO.getInfocode(), "10000")) {
                linkLocalStatsDO.setGid(gid).setFullShortUrl(fullShortUrl).setDate(today).setCnt(1);
                linkLocalStatsMapper.insertLinkLocalStats(linkLocalStatsDO);
            }
            // 操作系统
            linkOsStatsMapper.insertLinkOsStats(new LinkOsStatsDO()
                    .setGid(gid)
                    .setFullShortUrl(fullShortUrl)
                    .setDate(today)
                    .setOs(os));
            // 浏览器
            linkBrowserStatsMapper.insertLinkBrowserStats(new LinkBrowserStatsDO()
                    .setGid(gid)
                    .setFullShortUrl(fullShortUrl)
                    .setDate(today)
                    .setBrowser(browser));
            // 设备
            linkDeviceStatsMapper.insertLinkDeviceStats(new LinkDeviceStatsDO()
                    .setGid(gid)
                    .setFullShortUrl(fullShortUrl)
                    .setDate(today)
                    .setDevice(device));
            // 网络
            linkNetworkStatsMapper.insertLinkNetworkStats(new LinkNetworkStatsDO()
                    .setGid(gid)
                    .setFullShortUrl(fullShortUrl)
                    .setDate(today)
                    .setNetwork(network));

            // 访问日志
            linkAccessLogsMapper.insertAccessLog(new LinkAccessLogsDO()
                    .setUser(uv).setOs(os)
                    .setBrowser(browser)
                    .setIp(clientIp)
                    .setNetwork(network)
                    .setDevice(device)
                    .setLocale(province)
                    .setGid(gid).setFullShortUrl(fullShortUrl));
            // 今日统计
            linkStatsTodayMapper.insertLinkStatsToday(new LinkStatsTodayDO()
                    .setGid(gid)
                    .setFullShortUrl(fullShortUrl)
                    .setDate(today)
                    .setTodayUv(uvFirstFlag ? 1 : 0)
                    .setTodayIpCount(uipFirstFlag ? 1 : 0));
            // 历史统计
            shortLinkService.lambdaUpdate()
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getGid, gid)
                    .setSql("total_pv = total_pv + 1")
                    .setSql(uvFirstFlag, "total_up = total_up + 1")
                    .setSql(uipFirstFlag, "total_uip = total_uip + 1");
        } finally {
            rLock.unlock();
        }
    }
}
