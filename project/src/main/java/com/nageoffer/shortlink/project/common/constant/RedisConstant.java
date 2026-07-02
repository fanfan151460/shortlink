package com.nageoffer.shortlink.project.common.constant;

public class RedisConstant {

    public final static String LOCK_SHORT_LINK = "lock:Short-Link:%s";

    public final static String FULL_SHORT_LINK = "Full-Link:%s";

    public final static String LINK_STATS_UV = "Short-Link:UV:";

    public final static String LINK_STATS_UIP = "Short-Link:UIP:";

    public final static String LOCK_LINK_STATS = "lock:link-stats:%s";

    public static final String LOCK_GID_UPDATE_KEY = "LOCK:GROUP_UPDATE:%s";

    public static final String IDEMPOTENT_KEY = "IDEMPOTENT:%s";

    public static final String SHORT_URL_NULL_KEY = "SHORT_URL_NULL:%s";
}
