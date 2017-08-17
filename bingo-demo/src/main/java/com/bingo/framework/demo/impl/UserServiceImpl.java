package com.bingo.framework.demo.impl;

import com.bingo.framework.common.URL;
import com.bingo.framework.demo.bean.User;
import com.bingo.framework.demo.hello.UserService;

import java.util.List;

/**
 * Created by ZhangGe on 2017/7/8.
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(URL dst, Long id) {
        System.err.println("get user dst " + dst);
        System.err.println(id + " getUser");
        return new User(id, "getUser");
    }

    @Override
    public Long registerUser(User user) {
        System.err.println(user + " registerUser");
        return user.getId() + 1;
    }

    @Override
    public Long a(List<Long> ids) {
        System.out.println(ids);
        return ids.get(0);
    }


    public static void main(String[] args) {
        URL url = URL.valueOf("dubbo://10.20.153.11:20880/com.foo.BarService?application=bar&timeout=1000");
        System.out.println(url.toSiteString());
        System.out.println(url.getPath());
    }
}
