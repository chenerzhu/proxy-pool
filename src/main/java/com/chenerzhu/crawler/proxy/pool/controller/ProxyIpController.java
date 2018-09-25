package com.chenerzhu.crawler.proxy.pool.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.Result;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenerzhu
 * @create 2018-08-29 19:51
 **/
@Slf4j
@Controller
public class ProxyIpController extends BaseController {
    @Autowired
    private IProxyIpRedisService proxyIpRedisService;

    @Resource
    private IProxyIpService proxyIpService;

    @GetMapping("/")
    public String index(ModelMap modelMap){
        List proxyIpList=proxyIpRedisService.findAllByPageRt(0,20);
        modelMap.put("proxyIpList", JSON.toJSON(proxyIpList));
        return "index";
    }

    @GetMapping("/proxyIpLow")
    @ResponseBody
    public Object getProxyIpLow(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        ProxyIp proxyIp = proxyIpRedisService.getOne();
        boolean available = proxyIpService.testIp(proxyIp.getIp(), proxyIp.getPort(),proxyIp.getType());
        while (!available){
            proxyIp = proxyIpRedisService.getOne();
            available = proxyIpService.testIp(proxyIp.getIp(), proxyIp.getPort(),proxyIp.getType());
        }
        Result result=new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(Arrays.asList(proxyIp));
        return result;
    }

    @GetMapping("/proxyIp")
    @ResponseBody
    public Object getProxyIp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        ProxyIp proxyIp = proxyIpRedisService.getOneRt();
        Result result=new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(Arrays.asList(proxyIp));
        return result;
    }

    @PostMapping("/test")
    @ResponseBody
    public Object testIp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        String ip = request.getParameter("ip").trim();
        String port = request.getParameter("port").trim();
        boolean available = proxyIpService.testIp(ip, Integer.parseInt(port));
        Result result=new Result();
        result.setCode(200);
        result.setData(new ArrayList());
        result.setMessage(available==true?"available":"unavailable");
        return result;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}