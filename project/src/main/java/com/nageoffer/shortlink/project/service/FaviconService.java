package com.nageoffer.shortlink.project.service;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class FaviconService {

    private final ShortLinkMapper shortLinkMapper;

    private static final Pattern FAVICON_PATTERN = Pattern.compile(
            "<link[^>]+rel=[\"']?(?:shortcut )?icon[\"']?[^>]+href=[\"']([^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE);

    @Async("faviconExecutor")
    public void updateFavicon(String fullShortUrl, String gid, String originUrl) {
        try {
            String favicon = fetchFavicon(originUrl);
            LambdaUpdateWrapper<ShortLinkDO> wrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getGid, gid)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .set(ShortLinkDO::getFavicon, favicon);
            shortLinkMapper.update(null, wrapper);
        } catch (Exception e) {
            log.warn("异步获取图标失败: {}", originUrl, e);
        }
    }

    private String fetchFavicon(String originUrl) {
        try {
            String html = HttpUtil.get(originUrl);
            Matcher matcher = FAVICON_PATTERN.matcher(html);
            if (matcher.find()) {
                String href = matcher.group(1);
                if (href.startsWith("http") || href.startsWith("//")) {
                    return href.startsWith("//") ? "https:" + href : href;
                }
                URI uri = URI.create(originUrl);
                return uri.getScheme() + "://" + uri.getHost() + (href.startsWith("/") ? "" : "/") + href;
            }
        } catch (Exception ignored) {
        }
        URI uri = URI.create(originUrl);
        return uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
    }
}
