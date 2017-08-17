package com.bingo.framework.demo.client.network;

import com.bingo.framework.demo.client.ClientMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends NettyHandler {

    private final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private NettyClient nettyClient;

    public ClientHandler(NettyClient client) {
        this.nettyClient = client;
    }

    @Override
    protected void handleData(ChannelHandlerContext ctx, ClientMessage clientMessage) {
        System.err.println("handleData : " + clientMessage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        // ping(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        nettyClient.doConnect();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        nettyClient.doConnect();
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {

    }

    @Override
    protected void handleWriterIdle(ChannelHandlerContext ctx) {

    }


}