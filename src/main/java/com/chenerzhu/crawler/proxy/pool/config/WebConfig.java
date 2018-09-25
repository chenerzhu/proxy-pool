package com.chenerzhu.crawler.proxy.pool.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenerzhu
 * @create 2018-05-27 14:10
 **/
@Configuration
@EnableWebMvc // 启用MVC Java config的支持. 相当于 <mvc:annotation-driven/>
public class WebConfig implements WebMvcConfigurer {

    // 设置响应头信息
    private static List<MediaType> buildDefaultMediaTypes() {
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.TEXT_HTML); // 这个必须设置在第一位
        list.add(MediaType.APPLICATION_JSON_UTF8);
        return list;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
    }

    // 配置处理静态资源
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // 设置MessageConverter
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(stringHttpMessageConverter());
        converters.add(httpMessageConverter());
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        // 设置默认编码为UTF-8
        Charset default_charset = Charset.forName("UTF-8");
        StringHttpMessageConverter converter = new StringHttpMessageConverter(default_charset);
        List<MediaType> list = buildDefaultMediaTypes();
        converter.setSupportedMediaTypes(list);
        return converter;
    }
    @Bean
    public FastJsonHttpMessageConverter httpMessageConverter() {
        FastJsonHttpMessageConverter converter=new FastJsonHttpMessageConverter();
        List<MediaType> list = buildDefaultMediaTypes();
        converter.setSupportedMediaTypes(list);
        return converter;
    }
}