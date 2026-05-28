package com.nageoffer.shortlink.project.service;

public interface ILinkTitleService {

    /**
     * 获取目标网站标题
     * @param url 目标URL
     * @return 网站标题
     */
    String getTitleByUrl(String url);
}
