package com.chenerzhu.crawler.proxy.pool.listener;

import com.chenerzhu.crawler.proxy.pool.context.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * @author chenerzhu
 * @create 2018-08-31 10:50
 **/
@Slf4j
public class SpringContextListener extends ContextLoaderListener {
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        SpringContextHolder.initApplicationContext(webApplicationContext);
        log.debug("SpringContextListener contextInitialized");
    }

    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
        log.debug("SpringContextListener contextDestroyed");
    }
}