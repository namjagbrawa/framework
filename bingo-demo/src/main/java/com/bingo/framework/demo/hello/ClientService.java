package com.bingo.framework.demo.hello;

import com.bingo.framework.remoting.Channel;

/**
 * Created by ZhangGe on 2017/7/10.
 */
public interface ClientService {

    byte[] onClientMSG(int code, byte[] bytes, Channel channel);

    void onConnect(Channel channel);

    void onDisconnect(Channel channel);

}
