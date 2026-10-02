package com.nageoffer.shortlink.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBloomFilterConfiguration {

    /**
     * 预计最大用户数。位数组按容量一次性分配、不随元素增长，
     * 贴着预计量给、留 1~2 个数量级余量即可：1e6 ≈ 1.7MB。
     * 改这个值必须同时删掉 userName 和 {userName}:config 两个 key 才会生效。
     */
    public static final long EXPECTED_INSERTIONS = 1_000_000L;

    /**
     * 允许的误判率，决定约 14.4 位/元素（与容量无关）
     */
    public static final double FALSE_PROBABILITY = 0.001;

    /**
     * 防止用户注册查询数据库的布隆过滤器。
     * 这里只取 handle、不 tryInit：初始化时机交给 UserBloomFilterInitializer，
     * 否则配置会先于它的 @PostConstruct 被写进去，重建判断就永远拿到 false。
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        return redissonClient.getBloomFilter("userName");
    }
}
