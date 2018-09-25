package com.chenerzhu.crawler.proxy.pool.repository;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author chenerzhu
 * @create 2018-08-29 20:59
 **/
@Repository
public interface IProxyIpRepository extends JpaRepository<ProxyIp, Long> {
    Page<ProxyIp> findProxyIpsByAvailableIsTrue(Pageable pageable);

    long countProxyIpsByAvailableIsTrue();
    long countProxyIpsByAvailableIsTrueOrValidateCountIsBeforeOrValidateCountIsAfterAndAvailableRateIsAfter(int validateCountBefore,int validateCountAfters, double availableRate);

    ProxyIp findByIpEqualsAndPortEqualsAndTypeEquals(String ip, int port, String type);

    Page<ProxyIp> findProxyIpsByAvailableIsTrueOrValidateCountIsBeforeOrValidateCountIsAfterAndAvailableRateIsAfter(Pageable pageable, int validateCountBefore,int validateCountAfters, double availableRate);

    @Query("update ProxyIp set available=:available, " +
            "availableCount=:availableCount, " +
            "availableRate=:availableRate, " +
            "lastValidateTime=:lastValidateTime, " +
            "requestTime=:requestTime, " +
            "responseTime=:responseTime, " +
            "unAvailableCount=:unAvailableCount, " +
            "useTime=:useTime," +
            " validateCount=:validateCount where id=:id")
    @Modifying
    @Transactional
    Integer update(@Param("available") boolean available,
                      @Param("availableCount") Integer availableCount,
                      @Param("availableRate") double availableRate,
                      @Param("lastValidateTime") Date lastValidateTime,
                      @Param("requestTime") long requestTime,
                      @Param("responseTime") long responseTime,
                      @Param("unAvailableCount") int unAvailableCount,
                      @Param("useTime") long useTime,
                      @Param("validateCount") int validateCount,
                      @Param("id") long id);
}