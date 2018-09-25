package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.execute.impl.SchedulerJobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-21 15:03
 **/
@Slf4j
@Component
public class SchedulerJob implements Runnable {
    private static ISchedulerJobExecutor schedulerJobExecutor = new SchedulerJobExecutor(10, "schedulerJob");
    @Resource
    @Qualifier("syncDbSchedulerJob")
    private AbstractSchedulerJob syncDbSchedulerJob;
    @Resource
    @Qualifier("syncRedisSchedulerJob")
    private AbstractSchedulerJob syncRedisSchedulerJob;
    @Resource
    @Qualifier("validateRedisSchedulerJob")
    private AbstractSchedulerJob validateRedisSchedulerJob;
    @Override
    public void run() {
        try{
            schedulerJobExecutor.execute(syncDbSchedulerJob,10, 5, TimeUnit.SECONDS);
            schedulerJobExecutor.execute(syncRedisSchedulerJob,50, 30, TimeUnit.SECONDS);
            schedulerJobExecutor.execute(validateRedisSchedulerJob,100, 30, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("schedulerJob error:{}",e);
            schedulerJobExecutor.shutdown();
        }finally {

        }
    }
}
