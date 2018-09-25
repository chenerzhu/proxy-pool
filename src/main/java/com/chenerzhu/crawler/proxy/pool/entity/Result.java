package com.chenerzhu.crawler.proxy.pool.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author chenerzhu
 * @create 2018-09-05 22:09
 **/
@ToString
@Data
public class Result {
    private String message;
    private int code;
    private List data;
}