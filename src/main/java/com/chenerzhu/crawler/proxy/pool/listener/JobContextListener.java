package com.chenerzhu.crawler.proxy.pool.listener;

import com.chenerzhu.crawler.proxy.pool.job.crawler.CrawlerJob;
import com.chenerzhu.crawler.proxy.pool.job.scheduler.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author chenerzhu
 * @create 2018-08-30 12:33
 **/
@Slf4j
@WebListener
public class JobContextListener implements ServletContextListener {
    @Autowired
    private SchedulerJob schedulerJob;
    @Autowired
    private CrawlerJob crawlerJob;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.debug("JobContextListener contextInitialized");
        new Thread(schedulerJob).start();
        new Thread(crawlerJob).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.debug("JobContextListener contextDestroyed");
    }
}