package com.chenerzhu.crawler.proxy.pool.job.execute.impl;

import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.scheduler.AbstractSchedulerJob;
import com.chenerzhu.crawler.proxy.pool.thread.ThreadFactory;

import java.util.concurrent.*;

/**
 * @author chenerzhu
 * @create 2018-08-30 12:15
 **/
public class SchedulerJobExecutor implements ISchedulerJobExecutor {

    private ScheduledExecutorService scheduledExecutorService;
    public SchedulerJobExecutor(){}

    public SchedulerJobExecutor(String threadFactory){
        scheduledExecutorService=Executors.newScheduledThreadPool(10,new ThreadFactory(threadFactory));
    }

    public SchedulerJobExecutor(int corePoolSize,String threadFactory){
        scheduledExecutorService=Executors.newScheduledThreadPool(corePoolSize,new ThreadFactory(threadFactory));
    }


    public void execute(AbstractSchedulerJob schedulerJob, long delayTime, long intervalTime, TimeUnit timeUnit){
        scheduledExecutorService.scheduleAtFixedRate(schedulerJob,delayTime,intervalTime,timeUnit);
    }
    public void executeDelay(AbstractSchedulerJob schedulerJob, long delayTime, long intervalTime, TimeUnit timeUnit){
        scheduledExecutorService.scheduleWithFixedDelay(schedulerJob,delayTime,intervalTime,timeUnit);
    }

    public void shutdown(){
        scheduledExecutorService.shutdown();
    }
}