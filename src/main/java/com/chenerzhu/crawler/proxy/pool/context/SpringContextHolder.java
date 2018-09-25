package com.chenerzhu.crawler.proxy.pool.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author chenerzhu
 * @create 2018-08-30 21:09
 **/
@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext;

    private SpringContextHolder() {
    }

    public static void initApplicationContext(ApplicationContext applicationContext) {
        if(SpringContextHolder.applicationContext==null){
            SpringContextHolder.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(this.applicationContext==null){
            SpringContextHolder.applicationContext = applicationContext;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return (T) getApplicationContext().getBeansOfType(clazz);
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clear();
    }

    public static void clear() {
        log.debug("Clear ApplicationContext of  SpringContextHolder:" + applicationContext);
        applicationContext = null;
    }
}