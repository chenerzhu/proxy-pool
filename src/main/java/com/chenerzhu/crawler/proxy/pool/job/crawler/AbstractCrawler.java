package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.common.HttpMethod;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import com.chenerzhu.crawler.proxy.pool.job.scheduler.AbstractSchedulerJob;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author chenerzhu
 * @create 2018-09-02 13:40
 **/
@Slf4j
public abstract class AbstractCrawler extends AbstractSchedulerJob implements ICrawler, Runnable {
    protected ConcurrentLinkedQueue<ProxyIp> proxyIpQueue;
    protected String pageUrl;
    protected WebPage webPage;
    protected HttpMethod httpMethd=HttpMethod.GET;
    protected Map<String,String> formParamMap;
    private Map<String, String> headerMap = new HashMap<String, String>() {{
        put("Connection", "keep-alive");
        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        put("Accept-Encoding", "gzip, deflate, sdch");
        put("Accept-Language", "zh-CN,zh;q=0.9");
        put("Redis-Control", "max-age=0");
        put("Upgrade-Insecure-Requests", "1");
    }};

    public AbstractCrawler(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        this.proxyIpQueue = proxyIpQueue;
        this.pageUrl = pageUrl;
        this.httpMethd=HttpMethod.GET;
    }

    public AbstractCrawler(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl,HttpMethod httpMethd,Map<String,String> formParamMap) {
        this.proxyIpQueue = proxyIpQueue;
        this.pageUrl = pageUrl;
        this.httpMethd=httpMethd;
        this.formParamMap=formParamMap;
    }

    @Override
    public void run() {
        try {
            getPage();
            parsePage(webPage);
        }catch (Exception e){
            log.error("{} page process error",pageUrl,e);
        }

    }

    @Override
    public WebPage getPage() {
        WebPage webPage = null;
        try {
            log.debug("start get page:{}", pageUrl);
            headerMap.put("Referer", pageUrl);
            String pageContent="";
            if(httpMethd==HttpMethod.GET){
                pageContent= HttpClientUtils.sendGet(pageUrl, headerMap);
            }else if(httpMethd==HttpMethod.POST){
                pageContent= HttpClientUtils.sendPostForm(pageUrl, "",headerMap,formParamMap);
            }
            webPage = new WebPage();
            webPage.setCrawlTime(new Date());
            webPage.setPage(pageContent);
            webPage.setDocument(Jsoup.parse(pageContent));
            webPage.setHtml(Jsoup.parse(pageContent).html());
            this.webPage = webPage;
            log.debug("end get page:{}", pageUrl);
        } catch (Exception e) {
            log.error("get page:{}", pageUrl, e);
        }
        return webPage;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
}