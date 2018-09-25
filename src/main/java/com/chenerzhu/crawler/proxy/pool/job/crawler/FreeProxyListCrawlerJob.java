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
 * @create 2018-09-04 14:06
 * https://free-proxy-list.net/
 **/
@Slf4j
public class FreeProxyListCrawlerJob extends AbstractCrawler {
    public FreeProxyListCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementById("proxylisttable").getElementsByTag("tr");
        Element element;
        ProxyIp proxyIp;
        for (int i = 1; i < elements.size() - 1; i++) {
            try {
                element = elements.get(i);
                proxyIp = new ProxyIp();
                proxyIp.setIp(element.child(0).text());
                proxyIp.setPort(Integer.parseInt(element.child(1).text()));
                proxyIp.setLocation(element.child(2).text() + "-" + element.child(3).text());
                proxyIp.setType("yes".equalsIgnoreCase(element.child(6).text()) == true ? "https" : "http");
                proxyIp.setAvailable(true);
                proxyIp.setCreateTime(new Date());
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("freeProxyListCrawlerJob error:{0}",e);
            }
        }
    }
}