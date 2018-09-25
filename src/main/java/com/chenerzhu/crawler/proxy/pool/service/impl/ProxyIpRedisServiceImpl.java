package com.chenerzhu.crawler.proxy.pool.service.impl;

import com.chenerzhu.crawler.proxy.pool.common.RedisKey;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author chenerzhu
 * @create 2018-09-01 10:32
 **/
@Service
public class ProxyIpRedisServiceImpl implements IProxyIpRedisService {
    @Autowired
    private RedisTemplate<String, Serializable> redisCacheTemplate;

    @Override
    public boolean add(ProxyIp proxyIp) {
        return redisCacheTemplate.opsForZSet().add(RedisKey.PROXY_IP_KEY, proxyIp, proxyIp.getId());
    }

    @Override
    public Long remove(ProxyIp proxyIp) {
        return redisCacheTemplate.opsForZSet().removeRangeByScore(RedisKey.PROXY_IP_KEY, proxyIp.getId(), proxyIp.getId());
    }

    @Override
    public boolean isExist(ProxyIp proxyIp) {
        Set<Serializable> set = redisCacheTemplate.opsForZSet().rangeByScore(RedisKey.PROXY_IP_KEY, proxyIp.getId(), proxyIp.getId());
        if (set.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ProxyIp getOne() {
        int totalCount = (int) totalCountRt();
        int range=new Random().nextInt(totalCount);
        Set<Serializable> set = redisCacheTemplate.opsForZSet().range(RedisKey.PROXY_IP_KEY, range, range);
        return (ProxyIp) new ArrayList<Serializable>(set).get(0);
    }

    @Override
    public List<Serializable> findAllByPage(int pageNumber, int pageSize) {
        Set<Serializable> set = redisCacheTemplate.opsForZSet().range(RedisKey.PROXY_IP_KEY, pageNumber*pageSize, (pageNumber+1)*pageSize);
        return new ArrayList<Serializable>(set);
    }

    @Override
    public long totalCount() {
        return redisCacheTemplate.opsForZSet().size(RedisKey.PROXY_IP_KEY);
    }

    @Override
    public boolean addRt(ProxyIp proxyIp) {
        return redisCacheTemplate.opsForZSet().add(RedisKey.PROXY_IP_RT_KEY, proxyIp, proxyIp.getId());
    }

    @Override
    public Long removeRt(ProxyIp proxyIp) {
        return redisCacheTemplate.opsForZSet().removeRangeByScore(RedisKey.PROXY_IP_RT_KEY, proxyIp.getId(), proxyIp.getId());
    }

    @Override
    public boolean isExistRt(ProxyIp proxyIp) {
        Set<Serializable> set = redisCacheTemplate.opsForZSet().rangeByScore(RedisKey.PROXY_IP_RT_KEY, proxyIp.getId(), proxyIp.getId());
        if (set.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ProxyIp getOneRt() {
        int totalCount = (int) totalCountRt();
        int range=new Random().nextInt(totalCount);
        Set<Serializable> set = redisCacheTemplate.opsForZSet().range(RedisKey.PROXY_IP_RT_KEY, range, range);
        return (ProxyIp) new ArrayList<Serializable>(set).get(0);
    }

    @Override
    public List<Serializable> findAllByPageRt(int pageNumber, int pageSize) {
        Set<Serializable> set = redisCacheTemplate.opsForZSet().range(RedisKey.PROXY_IP_RT_KEY, pageNumber*pageSize, (pageNumber+1)*pageSize);
        return new ArrayList<Serializable>(set);
    }

    @Override
    public long totalCountRt() {
        return redisCacheTemplate.opsForZSet().size(RedisKey.PROXY_IP_RT_KEY);
    }
}