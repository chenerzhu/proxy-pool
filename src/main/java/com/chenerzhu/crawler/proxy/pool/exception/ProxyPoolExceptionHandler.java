package com.chenerzhu.crawler.proxy.pool.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author chenerzhu
 * @create 2018-08-29 20:29
 **/
@Slf4j
@ControllerAdvice
public class ProxyPoolExceptionHandler {
    @ExceptionHandler(ProxyPoolException.class)
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView processProxyPool(Exception e){
        log.info("自定义异常处理-ProxyPoolException");
        ModelAndView m = new ModelAndView();
        log.error("error：",e);
        m.addObject("exception", e.getMessage());
        m.setViewName("error/500");
        return m;
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView processException(Exception e){
        ModelAndView m = new ModelAndView();
        log.error("error：",e);
        m.addObject("exception", e.getMessage());
        m.setViewName("error/500");
        return m;
    }
}