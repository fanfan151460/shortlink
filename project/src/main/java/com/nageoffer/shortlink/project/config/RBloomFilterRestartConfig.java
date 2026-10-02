package com.nageoffer.shortlink.project.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 布隆过滤器启动预热。
 * 挂在 @PostConstruct 而不是 ApplicationRunner：跳转链路的 contains 是 fail-closed，
 * 空过滤器等于全站 404，所以必须在 web 端口打开、Nacos 注册之前建完。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RBloomFilterRestartConfig {

    private final RBloomFilter<String> bloomFilter;
    private final ShortLinkMapper shortLinkMapper;

    @PostConstruct
    public void init() {
        // true = 这次是我初始化的（key 原本不存在：新 Redis / 迁移删过 key）→ 过滤器是空的，要重建
        // false = 配置早已存在（正常重启）→ 内容还在，不扫表
        // ⚠️ 已知边界：配置正是 tryInit 写进去的，所以重建中途抛异常时 context 起不来，
        //    而下次启动 tryInit 返回 false 会跳过重建，带着空过滤器上线 —— 跳转链路
        //    fail-closed，等于全站 404。补救方向：失败时先删掉 {shortLink}:config 再抛出。
        if (bloomFilter.tryInit(RBloomFilterConfiguration.EXPECTED_INSERTIONS,
                RBloomFilterConfiguration.FALSE_PROBABILITY)) {
            log.warn("短链接布隆过滤器为空，开始重建");
            rebuild();
        }
    }

    private void rebuild() {
        // 不带 gid 会广播到 16 张分片表，这正是重建想要的
        // 不按 del_flag 过滤：布隆"宁可多加不可少加"，少加一条 = 那条链接永久 404
        List<String> fullShortUrls = shortLinkMapper.selectList(
                        new LambdaQueryWrapper<ShortLinkDO>().select(ShortLinkDO::getFullShortUrl)).stream()
                .map(ShortLinkDO::getFullShortUrl)
                .distinct()
                .toList();
        fullShortUrls.forEach(bloomFilter::add);
        log.warn("短链接布隆过滤器重建完成，写入 {} 条", fullShortUrls.size());
    }
}
