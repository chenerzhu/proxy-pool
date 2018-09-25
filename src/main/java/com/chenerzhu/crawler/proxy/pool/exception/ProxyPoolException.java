package com.chenerzhu.crawler.proxy.pool.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author chenerzhu
 * @create 2018-05-26 19:46
 **/
public class ProxyPoolException extends RuntimeException{
    public ProxyPoolException(){
        super();
    }

    public ProxyPoolException(String message){
        super(message);
    }

    public ProxyPoolException(String message,Throwable e){
        super(message,e);
    }
}