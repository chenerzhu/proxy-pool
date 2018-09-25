package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import com.chenerzhu.crawler.proxy.pool.job.crawler.AbstractCrawler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-08 16:35
 * https://www.my-proxy.com/free-proxy-list.html
 **/
@Slf4j
public class MyProxyCrawlerJob extends AbstractCrawler {
    public MyProxyCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        String[] elements = webPage.getDocument().getElementsByClass("list")
                .html().split("<br>");
        ProxyIp proxyIp;
        String element;
        for (int i = 0; i < 43; i++) {
            try {
                //185.120.37.186:55143#AL
                element = elements[i];
                String ipPort = element.split("#")[0];
                String ip = ipPort.split(":")[0];
                String port = ipPort.split(":")[1];
                String country = element.split("#")[1];
                proxyIp = new ProxyIp();
                proxyIp.setIp(ip);
                proxyIp.setPort(Integer.parseInt(port));
                proxyIp.setType("http");
                proxyIp.setCountry(country);
                proxyIp.setLocation(country);
                proxyIp.setCreateTime(new Date());
                proxyIp.setAvailable(true);
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("myProxyCrawlerJob error:{0}",e);
            }
        }


    }
}