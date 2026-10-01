package com.nageoffer.shortlink.project.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.shortlink.framework.exception.ServiceException;
import com.nageoffer.shortlink.project.dao.entity.*;
import com.nageoffer.shortlink.project.dao.mapper.*;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.mq.base.MessageWrapper;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.LINK_ACTIVITY_STATS_UV;
import static com.nageoffer.shortlink.project.common.constant.RedisConstant.LOCK_GID_UPDATE_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisConstant.STATS_SET_TTL_DAYS;

@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(
        topic = "${rocketmq.producer.topic}",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class LinkStatsConsumer implements RocketMQListener<MessageWrapper<ShortLinkStatsRecordDTO>> {

    private final ShortLinkGoToMapper shortLinkGoToMapper;
    private final LinkStatsMapper linkStatsMapper;
    private final LinkLocalStatsMapper linkLocalStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final ActivityStatsMapper activityStatsMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final IShortLinkService shortLinkService;
    private final MsgQueueIdempotentHandler idempotentHandler;

    @Value("${locale.gaoDe.apiKey}")
    private String apikey;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onMessage(MessageWrapper<ShortLinkStatsRecordDTO> message) {
        String msgKey = message.getKeys();
        if (!idempotentHandler.hasConsume(msgKey)) {
            // 第一次：SETNX 刚创建 key，开始消费
            String fullShortUrl = message.getMessage().getFullShortUrl();
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
            RLock rLock = readWriteLock.readLock();
            rLock.lock();
            // 本次消费往活动去重集合里 SADD 成功的 (key, member)。SADD 是 Redis 操作、不随事务回滚，
            // 所以回滚后必须补偿删除 —— 否则重投时 SADD 返回 0 → 判定"非首次" → 这次去重永远少算 1。
            // 只在"确实由本次新增"（added > 0）时才记录：added == 0 说明该成员属于更早的一次访问，不能删。
            AtomicReference<String> activitySetKey = new AtomicReference<>();
            AtomicReference<String> activitySetMember = new AtomicReference<>();
            try {
                transactionTemplate.execute(status -> {
                    consume(message.getMessage(), activitySetKey, activitySetMember);
                    return null;
                });
                idempotentHandler.successConsume(msgKey);  // "0" → "1"
            } catch (Throwable e) {
                if (activitySetKey.get() != null) {
                    stringRedisTemplate.opsForSet().remove(activitySetKey.get(), activitySetMember.get());
                }
                idempotentHandler.delConsume(msgKey);
                throw e;
            } finally {
                rLock.unlock();
            }
            return;
        }
        if (idempotentHandler.isSuccessConsume(msgKey)) {
            return;
        }
        throw new ServiceException("消息消费失败，消息队列重试");
    }

    private void consume(ShortLinkStatsRecordDTO dto,
                         AtomicReference<String> activitySetKey,
                         AtomicReference<String> activitySetMember) {
        String fullShortUrl = dto.getFullShortUrl();

        // 数据解析
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

        ShortLinkDO link = shortLinkService.lambdaQuery()
                .select(ShortLinkDO::getActivityId)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getGid, gid)
                .one();
        Long activityId = link == null ? null : link.getActivityId();
        if (activityId != null) {
            String activitySetKeyStr = String.format(LINK_ACTIVITY_STATS_UV, activityId, today);
            Long activityAdded = stringRedisTemplate.opsForSet().add(activitySetKeyStr, uv);
            stringRedisTemplate.expire(activitySetKeyStr, STATS_SET_TTL_DAYS, TimeUnit.DAYS);
            boolean activityFirstFlag = activityAdded != null && activityAdded > 0L;
            if (activityFirstFlag) {
                activitySetKey.set(activitySetKeyStr);
                activitySetMember.set(uv);
            }
            activityStatsMapper.insertActivityStats(new ActivityStatsDO()
                    .setActivityId(activityId)
                    .setDate(today)
                    .setUv(activityFirstFlag ? 1 : 0));
        }

        // pv uv uip
        LinkStatsDO statsDO = new LinkStatsDO()
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
            linkLocalStatsDO.setFullShortUrl(fullShortUrl).setDate(today).setCnt(1);
            if (StrUtil.isBlank(linkLocalStatsDO.getProvince())) {
                linkLocalStatsDO.setProvince("未知");
            }
            linkLocalStatsMapper.insertLinkLocalStats(linkLocalStatsDO);
        }
        // 操作系统
        linkOsStatsMapper.insertLinkOsStats(new LinkOsStatsDO()
                .setFullShortUrl(fullShortUrl)
                .setDate(today)
                .setOs(os));
        // 浏览器
        linkBrowserStatsMapper.insertLinkBrowserStats(new LinkBrowserStatsDO()
                .setFullShortUrl(fullShortUrl)
                .setDate(today)
                .setBrowser(browser));
        // 设备
        linkDeviceStatsMapper.insertLinkDeviceStats(new LinkDeviceStatsDO()
                .setFullShortUrl(fullShortUrl)
                .setDate(today)
                .setDevice(device));
        // 网络
        linkNetworkStatsMapper.insertLinkNetworkStats(new LinkNetworkStatsDO()
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
                .setFullShortUrl(fullShortUrl)
                .setDate(today)
                .setTodayUv(uvFirstFlag ? 1 : 0)
                .setTodayIpCount(uipFirstFlag ? 1 : 0));
        // 历史统计
        shortLinkService.lambdaUpdate()
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getGid, gid)
                .setSql("total_pv = total_pv + 1")
                .setSql(uvFirstFlag, "total_uv = total_uv + 1")
                .setSql(uipFirstFlag, "total_uip = total_uip + 1")
                .update();

    }
}
