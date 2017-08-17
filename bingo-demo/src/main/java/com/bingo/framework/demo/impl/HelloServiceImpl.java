package com.bingo.framework.demo.impl;

import com.bingo.framework.common.URL;
import com.bingo.framework.demo.hello.HelloService;

/**
 * Created by ZhangGe on 2017/7/7.
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(URL dst, String name) {
        System.err.println("hello dst " + dst);
        System.out.println("hello " + name);
        return "hello " + name;
    }

    @Override
    public String goodbye(URL dst, String name) {
        System.err.println("goodbye dst " + dst);
        System.out.println("goodbye " + name);
        return "goodbye " + name;
    }

}
