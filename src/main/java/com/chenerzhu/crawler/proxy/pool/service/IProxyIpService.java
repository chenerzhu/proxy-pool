package com.chenerzhu.crawler.proxy.pool.service;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;

import java.util.List;

/**
 * Created by chenerzhu on 2018/8/30.
 */
public interface IProxyIpService {
    ProxyIp save(ProxyIp proxyIp);

    List<ProxyIp> findAll();

    List<ProxyIp> findAllByPage(Integer pageNumber, Integer pageSize);

    long totalCount();

    long totalCount(int validateCountBefore,int validateCountAfter, double availableRate);

    List<ProxyIp> saveAll(List<ProxyIp> proxyIpList);

    void batchUpdate(List<ProxyIp> proxyIpList);

    void update(ProxyIp proxyIp);

    ProxyIp findByIpEqualsAndPortEqualsAndTypeEquals(String ip, int port, String type);

    List<ProxyIp> findAllByPage(Integer pageNumber, Integer pageSize, int validateCountBefore, int validateCountAfter, double availableRate);

    boolean testIp(String ip, int port);

    boolean testIp(String ip, int port, String type);
}
