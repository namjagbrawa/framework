package com.bingo.framework.demo.impl;

import com.bingo.framework.demo.hello.ClientService;
import com.bingo.framework.remoting.Channel;

/**
 * Created by ZhangGe on 2017/7/10.
 */
public class ClientServiceImpl implements ClientService {
    @Override
    public byte[] onClientMSG(int code, byte[] bytes, Channel channel) {

        System.err.println(code + " " + bytes + " " + channel);
        // return (code + " " + bytes + " " + channel).getBytes();
        return null;
    }

    @Override
    public void onConnect(Channel channel) {
        System.out.println("onConnect " + channel);

    }

    @Override
    public void onDisconnect(Channel channel) {
        System.out.println("onDisconnect " + channel);
    }
}
