package com.chenerzhu.crawler.proxy.pool.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenerzhu
 * @create 2018-09-10 20:27
 **/
public class ThreadFactory implements java.util.concurrent.ThreadFactory {

    private AtomicInteger counter = new AtomicInteger(0);
    private String name;

    public ThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable run) {
        Thread t = new Thread(run, name + "-t-" + counter);
        counter.incrementAndGet();
        return t;
    }
}