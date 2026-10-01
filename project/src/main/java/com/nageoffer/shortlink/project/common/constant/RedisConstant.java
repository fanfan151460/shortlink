package com.nageoffer.shortlink.project.common.constant;

public class RedisConstant {

    public final static String LOCK_SHORT_LINK = "lock:Short-Link:%s";

    public final static String FULL_SHORT_LINK = "Full-Link:%s";

    // 两个占位符依次为：fullShortUrl、访问日期（yyyy-MM-dd）
    public final static String LINK_STATS_UV = "Short-Link:UV:%s:%s";

    public final static String LINK_STATS_UIP = "Short-Link:UIP:%s:%s";

    // 两个占位符依次为：activityId、访问日期（yyyy-MM-dd）
    // 用途：活动级跨渠道去重 —— 同一个访客访问同一活动的多个渠道短链时，只有一个 id 落进这个集合
    public final static String LINK_ACTIVITY_STATS_UV = "Short-Link:Activity-UV:%s:%s";

    // 上述 UV/UIP/活动去重集合的 TTL：只需覆盖"当天"，留 1 天缓冲
    public static final long STATS_SET_TTL_DAYS = 2L;

    public final static String LOCK_LINK_STATS = "lock:link-stats:%s";

    public static final String LOCK_GID_UPDATE_KEY = "LOCK:GROUP_UPDATE:%s";

    public static final String IDEMPOTENT_KEY = "IDEMPOTENT:%s";

    public static final String SHORT_URL_NULL_KEY = "SHORT_URL_NULL:%s";
}
