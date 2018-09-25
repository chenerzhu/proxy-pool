package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-09 8:43
 * https://www.proxy4free.com/list/webproxy1.html
 **/
@Slf4j
public class Proxy4FreeCrawlerJob extends AbstractCrawler {
    public Proxy4FreeCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementsByTag("tr");
        Element element;
        ProxyIp proxyIp;
        for (int i = 2; i < elements.size(); i++) {
            try {
                element = elements.get(i);
                proxyIp = new ProxyIp();
                proxyIp.setIp(element.child(0).child(0).attr("href").replaceAll("\"", "").split("=")[1]);
                proxyIp.setPort(80);
                proxyIp.setLocation(element.child(3).text());
                proxyIp.setCountry(element.child(3).text());
                proxyIp.setAnonymity(element.child(9).text());
                proxyIp.setType("unKnow");
                proxyIp.setAvailable(true);
                proxyIp.setCreateTime(new Date());
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("proxy4FreeCrawlerJob error:{0}",e);
            }
        }

    }
}