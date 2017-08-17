package com.bingo.framework.remoting.transport.netty;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.Version;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.NamedThreadFactory;
import com.bingo.framework.common.utils.NetUtils;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.transport.AbstractClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.concurrent.TimeUnit;

/**
 * NettyClient.
 * 
 * @author qian.lei
 * @author william.liangf
 * @author wuwen
 */
public class NettyClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Bootstrap bootstrap;

    private volatile Channel channel; // volatile, please copy reference to use

    public NettyClient(final URL url, final ChannelHandler handler) throws RemotingException{
        super(url, wrapChannelHandler(url, handler));
    }

    private static final int DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", Constants.DEFAULT_IO_THREADS));

        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.eventLoopThreads: " + DEFAULT_EVENT_LOOP_THREADS);
        }
    }

    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new NamedThreadFactory("NettyClientTCPWorker", true));

    @Override
    protected void doOpen() throws Throwable {
        NettyHelper.setNettyLoggerFactory();
        bootstrap = new Bootstrap();
        // config
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(WORKER_GROUP);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout());
        final NettyHandler nettyHandler = new NettyHandler(getUrl(), this);
        bootstrap.handler(new ChannelInitializer() {
            public void initChannel(Channel ch) {
                NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyClient.this);
                ChannelPipeline channelPipeline = ch.pipeline();
                channelPipeline.addLast("decoder", adapter.getDecoder());
                channelPipeline.addLast("encoder", adapter.getEncoder());
                channelPipeline.addLast("handler", nettyHandler);
            }
        });

    }

    protected void doConnect() throws Throwable {
        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(getConnectAddress());
        try{
            boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
            
            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();

                try {
                    // 关闭旧的连接
                    Channel oldChannel = NettyClient.this.channel; // copy reference
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                            }
                            oldChannel.close().syncUninterruptibly();
                        } finally {
                            NettyChannel.removeChannelIfDisconnected(oldChannel);
                        }
                    }
                } finally {
                    if (NettyClient.this.isClosed()) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close new netty channel " + newChannel + ", because the client closed.");
                            }
                            newChannel.close().syncUninterruptibly();
                        } finally {
                            NettyClient.this.channel = null;
                            NettyChannel.removeChannelIfDisconnected(newChannel);
                        }
                    } else {
                        NettyClient.this.channel = newChannel;
                    }
                }
            } else if (future.cause() != null) {
                throw new RemotingException(this, "client(url: " + getUrl() + ") failed to connect to server "
                        + getRemoteAddress() + ", error message is:" + future.cause().getMessage(), future.cause());
            } else {
                throw new RemotingException(this, "client(url: " + getUrl() + ") failed to connect to server "
                        + getRemoteAddress() + " client-side timeout "
                        + getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from netty client "
                        + NetUtils.getLocalHost() + " using bingo version " + Version.getVersion());
            }
        }finally{
            if (! isConnected()) {
                future.cancel(true);
            }
        }
    }

    @Override
    protected void doDisConnect() throws Throwable {
        try {
            NettyChannel.removeChannelIfDisconnected(channel);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
    }
    
    @Override
    protected void doClose() throws Throwable {
        //WORKER_GROUP.shutdownGracefully()
    }

    @Override
    protected com.bingo.framework.remoting.Channel getChannel() {
        Channel c = channel;
        if (c == null || ! c.isActive())
            return null;
        return NettyChannel.getOrAddChannel(c, getUrl(), this);
    }

}