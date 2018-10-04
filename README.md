# proxy-pool 代理IP
### 背景
前段时间，写java爬虫来爬网易云音乐的评论。不料，爬了一段时间后ip被封禁了。由此，想到了使用ip代理，但是找了很多的ip代理网站，很少有可以用的代理ip。于是，抱着边学习的心态，自己开发了一个代理ip池。

### 相关技术及环境
**技术：** SpringBoot，SpringMVC, Hibernate,  MySQL, Redis , Maven, Lombok, BootStrap-table，多线程并发   
**环境：** JDK1.8 , IDEA

### 实现功能
通过ip代理池，提供高可用的代理ip,可用率达到95%以上。
-  通过接口获取代理ip
	通过访问接口，如：http://127.0.0.1:8080/proxyIp 返回代理ip的json格式
```json
{
    "code":200,
    "data":[
        {
            "available":true,
            "ip":"1.10.186.214",
            "lastValidateTime":"2018-09-25 20:31:52",
            "location":"THThailand",
            "port":57677,
            "requestTime":0,
            "responseTime":0,
            "type":"https",
            "useTime":3671
        }
    ],
    "message":"success"
}
```

-  通过页面获取代理ip
通过访问url，如：http://127.0.0.1:8080 返回代理ip列表页面。
<img width="690" height="400" src="https://github.com/chenerzhu/proxy-pool/blob/master/src/main/resources/static/img/home.PNG"/>    

-  提供代理ip测试接口及页面
通过访问url, 如：http://127.0.0.1:8080/test （get）测试代理ip的可用性；通过接口 http://127.0.0.1:8080/test ]（post  data: {"ip": "127.0.0.1","port":8080} ） 测试代理ip的可用性。
   
### 设计思路
#### 模块划分
-  爬虫模块：爬取代理ip网站的代理IP信息，先通过队列再保存进数据库。
-  数据库同步模块：设置一定时间间隔同步数据库IP到redis缓存中。
-  缓存redis同步模块：设置一定时间间隔同步redis缓存到另一块redis缓存中。
-  缓存redis代理ip校验模块：设置一定时间间隔redis缓存代理ip池校验。
-  前端显示及接口控制模块：显示可用ip页面，及提供ip获取api接口。

#### 架构图
<img width="700" height="500" src="https://github.com/chenerzhu/proxy-pool/blob/master/src/main/resources/static/img/crawler.PNG"/> 

### IP来源
代理ip均来自爬虫爬取，有些国内爬取的ip大多都不能用，代理池的ip可用ip大多是国外的ip。爬取的网站有：http://www.xicidaili.com/nn  ，http://www.data5u.com/free/index.shtml  ，https://free-proxy-list.net ，https://www.my-proxy.com/free-proxy-list.html ，http://spys.one/en/free-proxy-list/ ， https://www.proxynova.com/proxy-server-list/ ，https://www.proxy4free.com/list/webproxy1.html ，http://www.gatherproxy.com/ 。
### 如何使用
**前提：** 已经安装JDK1.8环境，MySQL数据库，Redis。  
先使用maven编译成jar,proxy-pool-1.0.jar。   
使用SpringBoot启动方式，启动即可。   
```java
java -jar proxy-pool-1.0.jar
```
