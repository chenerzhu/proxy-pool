package com.chenerzhu.crawler.proxy.pool.entity;

import lombok.Data;
import lombok.ToString;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenerzhu
 * @create 2018-09-02 15:14
 **/
@Data
@ToString
public class WebPage implements Serializable {
    private static final long serialVersionUID = 23454787L;
    private Date crawlTime;
    private String page;
    private Document document;
    private String html;
}