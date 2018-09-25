package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import com.chenerzhu.crawler.proxy.pool.job.crawler.AbstractCrawler;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-02 15:23
 * http://www.xicidaili.com
 **/
@Slf4j
public class XicidailiCrawlerJob extends AbstractCrawler {
    public XicidailiCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementsByTag("tr");
        Element element;
        ProxyIp proxyIp;
        for (int i = 1; i < elements.size(); i++) {
            try {
                element = elements.get(i);
                proxyIp = new ProxyIp();
                proxyIp.setIp(element.child(1).text());
                proxyIp.setPort(Integer.parseInt(element.child(2).text()));
                proxyIp.setLocation(element.child(3).text());
                proxyIp.setType(element.child(5).text());
                proxyIp.setAvailable(true);
                proxyIp.setCreateTime(new Date());
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("xicidailiCrawlerJob error:{0}",e);
            }
        }
    }
}