package com.chenerzhu.crawler.proxy.pool;

import com.chenerzhu.crawler.proxy.pool.common.RedisKey;
import com.chenerzhu.crawler.proxy.pool.context.SpringContextHolder;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by chenerzhu on 2018/9/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProxyPoolApplication.class)
public class ProxyPoolApplicationTest {
    @Autowired
    private RedisTemplate<String, Serializable> redisCacheTemplate;
    @Autowired
    private IProxyIpService proxyIpService;

    @Test
    public void testRedisExist() {
        Set<Serializable> set=redisCacheTemplate.opsForZSet().range(RedisKey.PROXY_IP_KEY,Long.parseLong("1535957777756"),Long.parseLong("1535957777756"));
        Set<Serializable> set1=redisCacheTemplate.opsForZSet().rangeByScore(RedisKey.PROXY_IP_KEY,Long.parseLong("1535957777756"),Long.parseLong("1535957777756"));
        System.out.println(set.size());
        System.out.println(set1.size());
        redisCacheTemplate.opsForZSet().remove(RedisKey.PROXY_IP_KEY,Long.parseLong("1535957777756"));
        redisCacheTemplate.opsForZSet().removeRangeByScore(RedisKey.PROXY_IP_KEY,Long.parseLong("1535961277498"),Long.parseLong("1535961277498"));
    }
    @Test
    public void testRedisGet(){
        Set<Serializable> set=redisCacheTemplate.opsForZSet().range(RedisKey.PROXY_IP_KEY,0,-1);

        System.out.println("size:"+set.size());
        for (Object obj:set){
            int count=0;
            ProxyIp proxyIp=(ProxyIp)obj;
            //System.out.println(proxyIp.toString());
            for (Object obj1:set){
                if(proxyIp.getId()==((ProxyIp)obj1).getId()){
                    System.out.println("==="+proxyIp.getId()+"=="+count++);
                }
            }
        }
    }

    @Test
    public void testSpringBean(){
        String name[]=SpringContextHolder.getApplicationContext().getBeanDefinitionNames();
        for (String name1:name){
            System.out.println(name1);
        }
    }


    @Test
    public void testRedisAdd(){
       // redisCacheTemplate.opsForZSet().add(RedisKey.PROXY_IP_RT_KEY,new ProxyIp(),1);
    }
    @Test
    public void testDBAdd(){
        ProxyIp proxyIp=new ProxyIp();
        proxyIp.setAvailableRate(1/(double)2);
        proxyIpService.save(proxyIp);
    }

    @Test
    public void testRedisCount(){
        long count=redisCacheTemplate.opsForZSet().size(RedisKey.PROXY_IP_RT_KEY);
        System.out.println(count);
        count=redisCacheTemplate.opsForZSet().count(RedisKey.PROXY_IP_RT_KEY, 0, Integer.MAX_VALUE);;
        System.out.println(count);
    }

//    @Test
//    public void testGetDbData(){
//        List<ProxyIp> proxyIpList=proxyIpService.findAllByPage(1,10,5,0.3);
//        System.out.println(proxyIpList.size());
//    }
}
