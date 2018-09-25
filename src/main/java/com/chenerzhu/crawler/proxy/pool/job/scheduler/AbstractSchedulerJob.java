package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.execute.impl.SchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.thread.ThreadFactory;
import com.chenerzhu.crawler.proxy.pool.util.ProxyUtils;

import java.util.concurrent.*;


/**
 * @author chenerzhu
 * @create 2018-08-30 10:27
 **/
public abstract class AbstractSchedulerJob implements Runnable {
    private volatile transient ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory("validate"));

    public Future<?> execute(Callable<?> callable) {
        initInstance();
        return executorService.submit(callable);
    }

    public Future<?> execute(FutureTask<?> task) {
        initInstance();
        return executorService.submit(task);
    }

    private void initInstance() {
        if (executorService.isShutdown()) {
            synchronized (AbstractSchedulerJob.class) {
                if (executorService.isShutdown()) {
                    executorService = Executors.newCachedThreadPool(new ThreadFactory("validate"));
                }
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public boolean validateIp(ProxyIp proxyIp) {
        boolean available = false;
        if (proxyIp.getType().toUpperCase().contains("HTTPS")) {
            available = ProxyUtils.validateHttps(proxyIp.getIp(), proxyIp.getPort());
        } else if (proxyIp.getType().toUpperCase().contains("HTTP")) {
            available = ProxyUtils.validateHttp(proxyIp.getIp(), proxyIp.getPort());
        } else if (proxyIp.getType().equalsIgnoreCase("unKnow")) {
            available = ProxyUtils.validateHttp(proxyIp.getIp(), proxyIp.getPort());
            if (!available) {
                available = ProxyUtils.validateHttps(proxyIp.getIp(), proxyIp.getPort());
            }
            /*if(!available){
                available = ProxyUtils.validateHttps(proxyIp.getIp(), proxyIp.getPort());
                proxyIp.setType("https");
            }
            if(!available){
                proxyIp.setType("unKnow");
            }*/
        } else if (proxyIp.getType().toUpperCase().contains("SOCKS")) {
            available = ProxyUtils.validateHttp(proxyIp.getIp(), proxyIp.getPort());
            if (!available) {
                available = ProxyUtils.validateHttps(proxyIp.getIp(), proxyIp.getPort());
            }
            /*if(!available){
                available = ProxyUtils.validateHttps(proxyIp.getIp(), proxyIp.getPort());
                proxyIp.setType("https");
            }
            if(!available){
                proxyIp.setType("socks");
            }*/
        }
        return available;
    }
}