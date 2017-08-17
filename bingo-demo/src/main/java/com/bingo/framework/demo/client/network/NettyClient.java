package com.bingo.framework.demo.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NettyClient {
    private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final static int READER_IDLE_TIME_SECONDS = 0; //无超时
    private final static int WRITER_IDLE_TIME_SECONDS = 0; //无超时
    private final static int ALL_IDLE_TIME_SECONDS = 15; //心跳15秒

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;

    private ChannelFuture future;
    private ChannelFutureListener channelFutureListener;

    private String ip;
    private int port;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Channel getChannel() {
        return channel;
    }

    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(READER_IDLE_TIME_SECONDS
                                    , WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
                            p.addLast(new MessageEncoder());
                            p.addLast(new MessageDecoder(2048, 4, 4));
                            p.addLast(new ClientHandler(NettyClient.this));
                        }
                    });
            doConnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Bootstrap getBootstrap() {
        return this.bootstrap;
    }

    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        this.future = bootstrap.connect(ip, port);

        this.channelFutureListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    logger.info("Connect to server successfully! " + ip + ":" + port);
                } else {
                    logger.error("Failed to connect to server, try connect after 10s");

                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        };

        this.future.addListener(channelFutureListener);
    }

    public void close() {
        try {
            ChannelFuture channelFuture1 = this.future.removeListener(channelFutureListener);
            logger.error("2" + channelFuture1);
            ChannelFuture channelFuture = future.awaitUninterruptibly();
            logger.error("1" + channelFuture);
            boolean cancel = this.future.cancel(true);
            logger.error("4" + cancel);
            boolean b = workGroup.awaitTermination(5, TimeUnit.SECONDS);
            logger.error("5" + b);
            logger.error("3");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}