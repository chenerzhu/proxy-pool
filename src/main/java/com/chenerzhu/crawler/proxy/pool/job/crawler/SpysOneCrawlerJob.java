package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.common.HttpMethod;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenerzhu
 * @create 2018-09-08 17:25
 * http://spys.one/en/free-proxy-list/
 * form:xpp=5&xf1=0&xf2=0&xf4=0&xf5=1
 **/
@Slf4j
public class SpysOneCrawlerJob extends AbstractCrawler {
    public SpysOneCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
        this.httpMethd=HttpMethod.POST;
        this.formParamMap=new HashMap(){{
            put("xpp","5");
            put("xf1","0");
            put("xf2","0");
            put("xf4","0");
            put("xf5","1");
        }};
    }

    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementsByClass("spy1xx");
        Element element;
        ProxyIp proxyIp;
        for (int i = 1; i < elements.size(); i++) {
            try {
                element = elements.get(i);
                proxyIp = new ProxyIp();
                proxyIp.setIp(element.child(0).selectFirst(".spy14").text());
                int port = getPort(element);
                if (port == -1) {
                    continue;
                }
                proxyIp.setPort(port);
                proxyIp.setCountry(element.child(3).selectFirst(".spy14").text());
                proxyIp.setLocation(element.child(3).text());
                proxyIp.setType(element.child(1).text());
                proxyIp.setAnonymity(element.child(2).text());
                proxyIp.setAvailable(true);
                proxyIp.setCreateTime(new Date());
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("spysOneCrawlerJob error:{0}",e);
            }
        }
    }

    private int getPort(Element element) throws ScriptException {
        int port = -1;
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Pattern pattern = Pattern.compile("\\+.*?<");
        Matcher matcher = null;
        Document document = webPage.getDocument();
        String scrpit = document.getElementsByTag("script").get(2).data();
        engine.eval(scrpit);
        matcher = pattern.matcher(element.child(0).html());
        if (matcher.find()) {
            String portScript = matcher.group(0).substring(1, matcher.group(0).length() - 2);
            Object obj=engine.eval(portScript.replaceAll("\\+", "+''+"));
            port = Integer.parseInt((String)obj);
        }
        return port;
    }

}