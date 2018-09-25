package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author chenerzhu
 * @create 2018-09-07 17:25
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class SyncDbSchedulerJob extends AbstractSchedulerJob {

    @Autowired
    private IProxyIpRedisService proxyIpRedisService;
    @Autowired
    private IProxyIpService proxyIpService;


    @Override
    public void run() {
        try {
            List<ProxyIp> availableIpList = new CopyOnWriteArrayList();
            List<ProxyIp> unAvailableIpList = new CopyOnWriteArrayList();
            int validateCountBefore = 3;
            int validateCountAfter = 100;
            double availableRate=0.5;//可用率大于0.5的重新取出来
            long totalCount = proxyIpService.totalCount(validateCountBefore,validateCountAfter,availableRate);
            log.info("proxyIp total count:{}", totalCount);
            AtomicInteger availableIpCount=new AtomicInteger(0);
            AtomicInteger unAvailableIpCount=new AtomicInteger(0);
            int pageSize = 200;
            int pageCount = (int) ((int) (totalCount % pageSize) == 0 ? totalCount / pageSize : totalCount / pageSize + 1);
            List<FutureTask<ProxyIp>> taskList = new ArrayList<>();
            long start = System.currentTimeMillis();
            IntStream.range(0, pageCount).forEach(pageNumber -> {
                List<ProxyIp> proxyIpList = proxyIpService.findAllByPage(pageNumber, pageSize, validateCountBefore,validateCountAfter ,availableRate);
                proxyIpList.forEach(proxyIp -> {
                    FutureTask task = new FutureTask(new Callable<ProxyIp>() {
                        @Override
                        public ProxyIp call() {
                            try{
                                long startTime = System.currentTimeMillis();
                                boolean available = validateIp(proxyIp);
                                long endTime = System.currentTimeMillis();
                                log.info("validateIp ==> ip:{} port:{}  available:{}  total time:{}", proxyIp.getIp(), proxyIp.getPort(), available, (endTime - startTime));
                                if (available) {
                                    if (proxyIpRedisService.isExist(proxyIp)) {
                                        log.info("redis exist ip:{}  port:{}", proxyIp.getIp(), proxyIp.getPort());
                                        proxyIpRedisService.remove(proxyIp);
                                    }
                                    proxyIp.setLastValidateTime(new Date());
                                    proxyIp.setAvailable(available);
                                    proxyIp.setValidateCount(proxyIp.getValidateCount() + 1);
                                    proxyIp.setAvailableCount(proxyIp.getAvailableCount()+1);
                                    proxyIp.setAvailableRate(proxyIp.getAvailableCount()/(double)proxyIp.getValidateCount());
                                    proxyIp.setUseTime(endTime - startTime);
                                    proxyIpRedisService.add(proxyIp);
                                    log.info("redis add or update ip:{}  port:{}", proxyIp.getIp(), proxyIp.getPort());
                                    availableIpList.add(proxyIp);
                                    availableIpCount.incrementAndGet();
                                } else {
                                    //proxyIpRedisService.remove(proxyIp);//第一层校验不删除缓存，通过第二层校验删除
                                    //log.info("redis remove ip:{}  port:{}", proxyIp.getIp(), proxyIp.getPort());
                                    proxyIp.setLastValidateTime(new Date());
                                    proxyIp.setAvailable(available);
                                    proxyIp.setValidateCount(proxyIp.getValidateCount() + 1);
                                    proxyIp.setUnAvailableCount(proxyIp.getUnAvailableCount()+1);
                                    proxyIp.setAvailableRate(proxyIp.getAvailableCount()/(double)proxyIp.getValidateCount());
                                    proxyIp.setUseTime(endTime - startTime);
                                    unAvailableIpList.add(proxyIp);
                                    unAvailableIpCount.incrementAndGet();
                                }
                                return proxyIp;
                            }catch (Exception e){
                                log.error("syncDb task proxyIP:{}",proxyIp.getIp(),e);
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
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
                    ProxyIp proxyIp = proxyIpFuture.get(6, TimeUnit.SECONDS);
                    if(proxyIp!=null){
                        proxyIpList.add(proxyIp);
                    }
                } catch (InterruptedException e) {
                    log.error("Interrupted ", e);
                } catch (Exception e) {
                    log.error("error:", e);
                }
            });
            refreshDataBase(availableIpList,unAvailableIpList);
            long end = System.currentTimeMillis();
            log.info("validate over total time:{}", (end - start));
            log.info("availableIp size:{}", availableIpCount.get());
            log.info("unAvailableIp size:{}", unAvailableIpCount.get());
        } catch (Exception e) {
            log.error("error:", e);
        } finally {
            shutdown();
        }
    }

    private void refreshDataBase(List<ProxyIp> availableIpList,List<ProxyIp> unAvailableIpList) {
        int batchSize = 100;
        List<FutureTask<ProxyIp>> taskList = new ArrayList<>();
        long startTime=System.currentTimeMillis();
        log.info("refreshDataBase start...");
        batchUpdate(availableIpList, batchSize, taskList);
        batchUpdate(unAvailableIpList, batchSize, taskList);

        taskList.forEach(proxyIpFuture -> {
            try {
                ProxyIp proxyIp = proxyIpFuture.get(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("refreshDataBase Interrupted ", e);
            } catch (Exception e) {
                log.error("refreshDataBase error:", e);
            }
        });
        long endTime=System.currentTimeMillis();
        log.info("refreshDataBase time:{}",endTime-startTime);
        log.info("refreshDataBase proxyIp size:{}", availableIpList.size()+unAvailableIpList.size());
    }

    private void batchUpdate(List<ProxyIp> ipList, int batchSize, List<FutureTask<ProxyIp>> taskList) {
        CopyOnWriteArrayList cowIpList=new CopyOnWriteArrayList(ipList);
        for (int i = 0; i < cowIpList.size(); i++) {
            if ((i != 0) && i % batchSize == 0 || (i + 1 == cowIpList.size())) {
                if(i<batchSize){
                    FutureTask task = new FutureTask(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            proxyIpService.batchUpdate(cowIpList);
                            return null;
                        }
                    });
                    taskList.add(task);
                    execute(task);
                }else{
                    final int start=i;
                    FutureTask task = new FutureTask(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            proxyIpService.batchUpdate(cowIpList.subList(start-batchSize,start));
                            return null;
                        }
                    });
                    taskList.add(task);
                    execute(task);
                }
            }
        }
    }

}