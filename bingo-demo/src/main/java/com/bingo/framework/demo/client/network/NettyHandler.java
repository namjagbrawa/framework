package com.bingo.framework.demo.client.network;

import com.bingo.framework.demo.client.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class NettyHandler extends SimpleChannelInboundHandler<ClientMessage> {

    private int heartbeatCount = 0;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientMessage msg) throws Exception {
        handleData(ctx, msg);
    }

    protected void ping(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(8);
        buf.writeShort(ClientMessage.hbreq);
        buf.writeShort(ClientMessage.magic);
        buf.writeInt(0);
        context.writeAndFlush(buf);
    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ClientMessage netMessage);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public abstract void channelActive(ChannelHandlerContext ctx) throws Exception;

    @Override
    public abstract void channelInactive(ChannelHandlerContext ctx) throws Exception;

    protected abstract void handleReaderIdle(ChannelHandlerContext ctx);

    protected abstract void handleWriterIdle(ChannelHandlerContext ctx);

    protected abstract void handleAllIdle(ChannelHandlerContext ctx);
}