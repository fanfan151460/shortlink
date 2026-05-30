package com.nageoffer.shortlink.project.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Optional;

public class LinkUtil {

    public static Long getLinkExpireTime(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(each -> each.getTime() - new Date().getTime())
                .orElse(2592000000L);
    }

    public static String getOs(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtil.isBlank(ua)) {
            return "其他";
        }
        if (ua.contains("Android")) return "Android";
        if (ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iOS")) return "iOS";
        if (ua.contains("Windows")) return "Windows";
        if (ua.contains("Mac OS X") || ua.contains("Macintosh")) return "macOS";
        if (ua.contains("Linux")) return "Linux";
        return "其他";
    }

    /**
     * 获取访问设备类型
     */
    public static String getDevice(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtil.isBlank(ua)) {
            return "PC";
        }
        if (ua.contains("Android") || ua.contains("iPhone") || ua.contains("iPad")
                || ua.contains("Mobile") || ua.contains("iPod")) {
            return "Mobile";
        }
        return "PC";
    }

    public static String getBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtil.isBlank(ua)) {
            return "其他";
        }
        if (ua.contains("Edg/")) return "Edge";
        if (ua.contains("OPR/")) return "Opera";
        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Safari")) return "Safari";
        if (ua.contains("Firefox")) return "Firefox";
        if (ua.contains("MSIE") || ua.contains("Trident")) return "IE";
        return "其他";
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取访问网络类型
     */
    public static String getNetwork(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtil.isBlank(ua)) {
            return "未知";
        }
        if (ua.contains("Android") || ua.contains("iPhone") || ua.contains("iPad")
                || ua.contains("Mobile") || ua.contains("iPod")) {
            return "移动网络";
        }
        return "WIFI";
    }

    /**
     *  获取指定ip位置
     */
    public static String getLocalByIp(String key, String ip) {
        String url = "https://restapi.amap.com/v3/ip?ip=" + ip + "&key=" + key;
        return HttpUtil.get(url);
    }

}
