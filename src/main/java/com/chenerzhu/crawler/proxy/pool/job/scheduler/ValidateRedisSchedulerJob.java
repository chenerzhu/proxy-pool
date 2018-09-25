package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author chenerzhu
 * @create 2018-09-06 10:53
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class ValidateRedisSchedulerJob extends AbstractSchedulerJob {
    @Autowired
    private IProxyIpRedisService proxyIpRedisService;

    @Override
    public void run() {
        try {
            List<ProxyIp> availableIpList = new CopyOnWriteArrayList();
            List<ProxyIp> unAvailableIpList = new CopyOnWriteArrayList();
            long redisTotalCount = proxyIpRedisService.totalCountRt();
            log.info("the redis's proxyIp total count:{}", redisTotalCount);
            int pageSize = 100;
            int pageCount = (int) ((int) (redisTotalCount % pageSize) == 0 ? redisTotalCount / pageSize : redisTotalCount / pageSize + 1);
            List<FutureTask<ProxyIp>> taskList = new ArrayList<>();
            long start = System.currentTimeMillis();
            IntStream.range(0, pageCount).forEach(pageNumber -> {

                List<Serializable> proxyIpList = proxyIpRedisService.findAllByPageRt(pageNumber, pageSize);
                proxyIpList.forEach(serializable -> {
                    FutureTask task = new FutureTask(new Callable<ProxyIp>() {
                        ProxyIp proxyIp = (ProxyIp) serializable;

                        @Override
                        public ProxyIp call() {
                            try{
                                long startTime = System.currentTimeMillis();
                                boolean available = validateIp(proxyIp);
                                long endTime = System.currentTimeMillis();
                                log.info("validateIp redis rt ==> ip:{} port:{}  available:{}  total time:{}", proxyIp.getIp(), proxyIp.getPort(), available, (endTime - startTime));
                                if (available) {
                                    if (proxyIpRedisService.isExistRt(proxyIp)) {
                                        log.info("redis rt exist ip:{}  port:{}", proxyIp.getIp(), proxyIp.getPort());
                                        proxyIpRedisService.removeRt(proxyIp);
                                    }
                                    proxyIp.setLastValidateTime(new Date());
                                    proxyIp.setAvailable(available);
                                    proxyIp.setValidateCount(proxyIp.getValidateCount() + 1);
                                    proxyIp.setAvailableCount(proxyIp.getAvailableCount() + 1);
                                    proxyIp.setAvailableRate(proxyIp.getAvailableCount() / (double) proxyIp.getValidateCount());
                                    proxyIp.setUseTime(endTime - startTime);
                                    proxyIpRedisService.addRt(proxyIp);
                                    log.info("redis rt  add or update ip:{}  port:{}", proxyIp.getIp(), proxyIp.getPort());
                                    availableIpList.add(proxyIp);
                                } else {
                                    proxyIpRedisService.removeRt(proxyIp);
                                    log.info("redis rt  remove ip:{}  port:{}", proxyIp.getIp(), proxyIp.getPort());
                                    proxyIp.setLastValidateTime(new Date());
                                    proxyIp.setAvailable(available);
                                    proxyIp.setValidateCount(proxyIp.getValidateCount() + 1);
                                    proxyIp.setUnAvailableCount(proxyIp.getUnAvailableCount() + 1);
                                    proxyIp.setAvailableRate(proxyIp.getAvailableCount() / (double) proxyIp.getValidateCount());
                                    proxyIp.setUseTime(endTime - startTime);
                                    unAvailableIpList.add(proxyIp);
                                }
                                return proxyIp;
                            }catch (Exception e){
                                log.error("syncRedis task proxyIP:{}",proxyIp.getIp(),e);
                            }
                            return null;

                        }
                    });
                    taskList.add(task);
                    execute(task);
                });
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            });
            List<ProxyIp> proxyIpList = new ArrayList<>();
            taskList.forEach(proxyIpFuture -> {
                try {
                    ProxyIp proxyIp = proxyIpFuture.get(10, TimeUnit.SECONDS);
                    if(proxyIp!=null){
                        proxyIpList.add(proxyIp);
                    }
                } catch (InterruptedException e) {
                    log.error("Interrupted ", e);
                } catch (Exception e) {
                    log.error("error:", e);
                }
            });
            long end = System.currentTimeMillis();
            log.info("the redis's ip validate over, total time:{}", (end - start));
            log.info("the redis's availableIp size:{}", availableIpList.size());
            log.info("the redis's unAvailableIp size:{}", unAvailableIpList.size());
            log.info("refresh redis's proxyIp size:{}", proxyIpList.size());
        } catch (Exception e) {
            log.error("error:", e);
        } finally {
            shutdown();
        }
    }
}