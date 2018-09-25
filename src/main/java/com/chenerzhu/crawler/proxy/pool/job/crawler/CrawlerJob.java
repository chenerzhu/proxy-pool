package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.execute.impl.SchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import com.chenerzhu.crawler.proxy.pool.thread.ThreadFactory;

/**
 * @author chenerzhu
 * @create 2018-09-02 20:16
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class CrawlerJob implements Runnable {
    private volatile static ExecutorService executorService= Executors.newFixedThreadPool(5,new ThreadFactory("crawlerJob-consumer"));

    private ISchedulerJobExecutor schedulerJobExecutor=new SchedulerJobExecutor(30,"crawlerJob-producer");

    @Autowired
    private IProxyIpService proxyIpService;

    @Override
    public void run() {
        try{
            ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();
            //生产者
            //schedulerJobExecutor.execute(new XicidailiCrawlerJob(proxyIpQueue, "http://www.xicidaili.com/nn"), 0, 100, TimeUnit.SECONDS);

            //schedulerJobExecutor.execute(new Data5uCrawlerJob(proxyIpQueue, "http://www.data5u.com/free/index.shtml"), 10, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new FreeProxyListCrawlerJob(proxyIpQueue, "https://free-proxy-list.net"), 20, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new MyProxyCrawlerJob(proxyIpQueue, "https://www.my-proxy.com/free-proxy-list.html"), 30, 100, TimeUnit.SECONDS);

            //schedulerJobExecutor.execute(new SpysOneCrawlerJob(proxyIpQueue, "http://spys.one/en/free-proxy-list/"), 40, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new ProxynovaCrawlerJob(proxyIpQueue, "https://www.proxynova.com/proxy-server-list/"), 50, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new Proxy4FreeCrawlerJob(proxyIpQueue, "https://www.proxy4free.com/list/webproxy1.html"), 60, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new GatherproxyCrawlerJob(proxyIpQueue, "http://www.gatherproxy.com/"), 70, 100, TimeUnit.SECONDS);

            //消费者
            for (int i = 0; i < 5; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (true && !Thread.currentThread().isInterrupted()) {
                            try {
                                log.info("the proxyIpQueue current  size:{}", proxyIpQueue.size());
                                ProxyIp proxyIp = proxyIpQueue.poll();
                                if (proxyIp != null) {
                                    log.debug("get proxy ip:{}", proxyIp.toString());
                                    if (proxyIpService.findByIpEqualsAndPortEqualsAndTypeEquals(proxyIp.getIp(), proxyIp.getPort(), proxyIp.getType()) == null) {
                                        proxyIpService.save(proxyIp);
                                    } else {
                                        log.debug("the proxy ip exist:{}", proxyIp.toString());
                                    }
                                }else{
                                    TimeUnit.SECONDS.sleep(3);
                                }
                            } catch (Exception e) {
                                log.error("get the proxy ip  failed! error:{}",e.getMessage());
                                //e.printStackTrace();
                                try {
                                    TimeUnit.SECONDS.sleep(3);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            log.error("crawler error:{}",e);
            executorService.shutdown();
            schedulerJobExecutor.shutdown();
        }finally {

        }
    }
}