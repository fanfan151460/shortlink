package com.nageoffer.shortlink.project.common.constant;

public class RedisConstant {

    public final static String LOCK_SHORT_LINK = "lock:Short-Link:%s";

    public final static String FULL_SHORT_LINK = "Full-Link:%s";

    // 两个占位符依次为：fullShortUrl、访问日期（yyyy-MM-dd）
    public final static String LINK_STATS_UV = "Short-Link:UV:%s:%s";

    public final static String LINK_STATS_UIP = "Short-Link:UIP:%s:%s";

    public final static String LOCK_LINK_STATS = "lock:link-stats:%s";

    public static final String LOCK_GID_UPDATE_KEY = "LOCK:GROUP_UPDATE:%s";

    public static final String IDEMPOTENT_KEY = "IDEMPOTENT:%s";

    public static final String SHORT_URL_NULL_KEY = "SHORT_URL_NULL:%s";
}
