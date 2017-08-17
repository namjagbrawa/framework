package com.bingo.framework.demo.client;

import com.bingo.framework.demo.client.network.NettyClient;
import io.netty.channel.Channel;

/**
 * Created by ZhangGe on 2017/7/10.
 */
public class ClientController {

    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient("192.168.221.200", 1234);
        nettyClient.start();

        Channel channel = nettyClient.getChannel();
        if (channel == null) {
            while (true) {
                channel = nettyClient.getChannel();
                if (channel != null) {
                    break;
                }
            }
        }

        for (int i = 0; i< 100; i ++) {
            channel.writeAndFlush(new ClientMessage(1, null));
            Thread.sleep(1000);
        }
        System.out.println("send complete");

    }
}
