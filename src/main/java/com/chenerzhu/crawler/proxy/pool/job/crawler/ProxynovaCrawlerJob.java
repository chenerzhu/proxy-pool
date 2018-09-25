package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenerzhu
 * @create 2018-09-08 23:25
 * https://www.proxynova.com/proxy-server-list/
 **/
@Slf4j
public class ProxynovaCrawlerJob extends AbstractCrawler {
    public ProxynovaCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementsByTag("tbody")
                .get(0).getElementsByTag("tr");
        Element element;
        ProxyIp proxyIp;
        for (int i = 0; i < elements.size(); i++) {
            try {
                element = elements.get(i);
                proxyIp = new ProxyIp();
                String ip = getIp(element);
                if ("".equals(ip)) {
                    continue;
                }
                proxyIp.setIp(ip);
                proxyIp.setPort(Integer.parseInt(element.child(1).text()));
                proxyIp.setLocation(element.child(5).text());
                proxyIp.setCountry(element.child(5).text().split("-")[0]);
                proxyIp.setAnonymity(element.child(6).text());
                proxyIp.setType("unKnow");
                proxyIp.setAvailable(true);
                proxyIp.setCreateTime(new Date());
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("proxynovaCrawlerJob error:{0}",e);
            }
        }
    }

    private String getIp(Element element) throws ScriptException {
        String ip = "";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Pattern pattern = Pattern.compile("\\(.*?\\);<");
        Matcher matcher = null;
        matcher = pattern.matcher(element.child(0).html());
        if (matcher.find()) {
            String ipScript = matcher.group(0).substring(1, matcher.group(0).length() - 1);
            ip = (String) engine.eval(ipScript.replaceAll("\\);", ""));
        }
        return ip;
    }
}