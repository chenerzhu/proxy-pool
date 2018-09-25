package com.chenerzhu.crawler.proxy.pool.job.execute;

import com.chenerzhu.crawler.proxy.pool.job.scheduler.AbstractSchedulerJob;

import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-08-30 12:14
 **/
public interface ISchedulerJobExecutor {
    void execute(AbstractSchedulerJob schedulerJob, long delayTime, long intervalTime, TimeUnit timeUnit);
    void executeDelay(AbstractSchedulerJob schedulerJob, long delayTime, long intervalTime, TimeUnit timeUnit);
    void shutdown();
    //void execute(Runnable runnable);
}