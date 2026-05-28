package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.nageoffer.shortlink.project.service.ILinkTitleService;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LinkTitleServiceImpl implements ILinkTitleService {

    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "<title[^>]*>(.*?)</title>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public String getTitleByUrl(String url) {
        String html = HttpUtil.get(url);
        Matcher matcher = TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return StrUtil.EMPTY;
    }
}
