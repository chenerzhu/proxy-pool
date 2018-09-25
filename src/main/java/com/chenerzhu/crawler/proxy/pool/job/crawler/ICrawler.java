package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.WebPage;

/**
 * @author chenerzhu
 * @create 2018-09-02 13:40
 **/
public interface ICrawler {
    WebPage getPage();

    void parsePage(WebPage webPage);
}