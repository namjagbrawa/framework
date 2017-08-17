package com.bingo.framework.remoting.transport.netty;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.ExecutorUtil;
import com.bingo.framework.common.utils.NamedThreadFactory;
import com.bingo.framework.common.utils.NetUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.Server;
import com.bingo.framework.remoting.transport.AbstractServer;
import com.bingo.framework.remoting.transport.dispatcher.ChannelHandlers;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * NettyServer
 *
 * @author qian.lei
 * @author chao.liuc
 * @author wuwen
 */
public class NettyServer extends AbstractServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Map<String, Channel>  channels; // <ip:port, channel>

    private io.netty.channel.Channel channel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public NettyServer(URL url, ChannelHandler handler) throws RemotingException{
        super(url, ChannelHandlers.wrap(handler, ExecutorUtil.setThreadName(url, SERVER_THREAD_POOL_NAME)));
    }

    @Override
    protected void doOpen() throws Throwable {
        NettyHelper.setNettyLoggerFactory();
        ServerBootstrap bootstrap = new ServerBootstrap();

        final NettyHandler nettyHandler = new NettyHandler(getUrl(), this);
        channels = nettyHandler.getChannels();

        bossGroup = new NioEventLoopGroup(1, (new NamedThreadFactory("NettyServerBoss", true)));
        workerGroup = new NioEventLoopGroup(getUrl().getPositiveParameter(Constants.IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS), new NamedThreadFactory("NettyServerWorker", true));

        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, false);
        bootstrap.childHandler(new ChannelInitializer() {

            public void initChannel(io.netty.channel.Channel ch) {
                NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyServer.this);
                ChannelPipeline channelPipeline = ch.pipeline();
                channelPipeline.addLast("decoder", adapter.getDecoder());
                channelPipeline.addLast("encoder", adapter.getEncoder());
                channelPipeline.addLast("handler", nettyHandler);
            }
        });


        // bind
        ChannelFuture channelFuture = bootstrap.bind(getBindAddress());

        channelFuture.awaitUninterruptibly();
        channel = channelFuture.channel();
    }

    @Override
    protected void doClose() throws Throwable {
        try {
            if (channel != null) {
                // unbind.
                channel.close().syncUninterruptibly();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            Collection<com.bingo.framework.remoting.Channel> channels = getChannels();
            if (channels != null && channels.size() > 0) {
                for (com.bingo.framework.remoting.Channel channel : channels) {
                    try {
                        channel.close();
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            // and then shutdown the thread pools
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (channels != null) {
                channels.clear();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public Collection<Channel> getChannels() {
        Collection<Channel> chs = new HashSet<Channel>();
        for (Channel channel : this.channels.values()) {
            if (channel.isConnected()) {
                chs.add(channel);
            } else {
                channels.remove(NetUtils.toAddressString(channel.getRemoteAddress()));
            }
        }
        return chs;
    }

    public Channel getChannel(InetSocketAddress remoteAddress) {
        return channels.get(NetUtils.toAddressString(remoteAddress));
    }

    public boolean isBound() {
        return channel.isRegistered();
    }

}