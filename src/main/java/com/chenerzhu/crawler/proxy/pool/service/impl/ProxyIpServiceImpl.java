package com.chenerzhu.crawler.proxy.pool.service.impl;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.repository.IProxyIpRepository;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import com.chenerzhu.crawler.proxy.pool.util.ProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * @author chenerzhu
 * @create 2018-08-30 19:05
 **/
@Service("proxyIpService")
public class ProxyIpServiceImpl implements IProxyIpService {
    @Autowired
    private IProxyIpRepository proxyIpRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProxyIp save(ProxyIp proxyIp) {
        return proxyIpRepository.save(proxyIp);
    }
    @Override
    public List<ProxyIp> findAll() {
        return proxyIpRepository.findAll();
    }

    @Override
    public List<ProxyIp> findAllByPage(Integer pageNumber, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return proxyIpRepository.findProxyIpsByAvailableIsTrue(pageable).getContent();
    }
    @Override
    public long totalCount(){
        return proxyIpRepository.countProxyIpsByAvailableIsTrue();
    }

    @Override
    public long totalCount(int validateCountBefore,int validateCountAfter, double availableRate){
        return proxyIpRepository.countProxyIpsByAvailableIsTrueOrValidateCountIsBeforeOrValidateCountIsAfterAndAvailableRateIsAfter(validateCountBefore,validateCountAfter, availableRate);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProxyIp> saveAll(List<ProxyIp> proxyIpList){
        return proxyIpRepository.saveAll(proxyIpList);
    }

    /*@Transactional(rollbackFor = Exception.class)*/
    @Override
    public void batchUpdate(List<ProxyIp> proxyIpList) {
        for(int i = 0; i < proxyIpList.size(); i++) {
            update(proxyIpList.get(i));
        }
    }
    @Override
    public void update(ProxyIp proxyIp){
        proxyIpRepository.update(proxyIp.isAvailable(),
                proxyIp.getAvailableCount(),
                proxyIp.getAvailableRate(),
                proxyIp.getLastValidateTime(),
                proxyIp.getRequestTime(),
                proxyIp.getResponseTime(),
                proxyIp.getUnAvailableCount(),
                proxyIp.getUseTime(),
                proxyIp.getValidateCount(),
                proxyIp.getId()
        );

    }



    @Override
    public ProxyIp findByIpEqualsAndPortEqualsAndTypeEquals(String ip, int port, String type) {
        return proxyIpRepository.findByIpEqualsAndPortEqualsAndTypeEquals(ip,port,type);
    }
    @Override
    public List<ProxyIp> findAllByPage(Integer pageNumber, Integer pageSize, int validateCountBefore,int validateCountAfter, double availableRate){
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return proxyIpRepository.findProxyIpsByAvailableIsTrueOrValidateCountIsBeforeOrValidateCountIsAfterAndAvailableRateIsAfter(pageable,validateCountBefore,validateCountAfter,availableRate).getContent();
    }
    @Override
    public boolean testIp(String ip, int port){
        boolean available= ProxyUtils.validateIp(ip,port, ProxyUtils.ProxyType.HTTP);
        if(!available){
            available= ProxyUtils.validateIp(ip,port, ProxyUtils.ProxyType.HTTPS);
        }
        return available;
    }
    @Override
    public boolean testIp(String ip, int port, String type){
        if("http".equalsIgnoreCase(type)){
            return ProxyUtils.validateIp(ip,port, ProxyUtils.ProxyType.HTTP);
        }else{
            return ProxyUtils.validateIp(ip,port, ProxyUtils.ProxyType.HTTPS);
        }

    }
}