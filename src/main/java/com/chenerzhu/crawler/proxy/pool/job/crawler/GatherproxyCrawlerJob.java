package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenerzhu
 * @create 2018-09-09 9:09
 * http://www.gatherproxy.com/
 **/
@Slf4j
public class GatherproxyCrawlerJob extends AbstractCrawler {
    public GatherproxyCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        Pattern pattern = Pattern.compile("\\{\"PROXY_CITY\".*?\"}");
        Matcher matcher = null;
        matcher = pattern.matcher(webPage.getHtml());
        ProxyIp proxyIp = null;
        while (matcher.find()) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(matcher.group(0));
                proxyIp = new ProxyIp();
                proxyIp.setIp(jsonObject.getString("PROXY_IP"));
                proxyIp.setPort(Integer.parseInt(jsonObject.getString("PROXY_PORT"), 16));
                proxyIp.setType("SOCKS");//
                proxyIp.setLocation(jsonObject.getString("PROXY_COUNTRY"));
                proxyIp.setCountry(jsonObject.getString("PROXY_COUNTRY"));
                proxyIp.setAnonymity(jsonObject.getString("PROXY_TYPE"));
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