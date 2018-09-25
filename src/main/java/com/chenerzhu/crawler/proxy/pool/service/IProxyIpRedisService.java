package com.chenerzhu.crawler.proxy.pool.service;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;

import java.io.Serializable;
import java.util.List;

/**
 * @author chenerzhu
 * @create 2018-09-01 10:31
 **/
public interface IProxyIpRedisService {
    boolean add(ProxyIp proxyIp);

    Long remove(ProxyIp proxyIp);

    boolean isExist(ProxyIp proxyIp);

    ProxyIp getOne();

    List<Serializable> findAllByPage(int pageNumber, int pageSize);

    long  totalCount();

    boolean addRt(ProxyIp proxyIp);

    Long removeRt(ProxyIp proxyIp);

    boolean isExistRt(ProxyIp proxyIp);

    ProxyIp getOneRt();

    List<Serializable> findAllByPageRt(int pageNumber, int pageSize);

    long totalCountRt();
}