package com.bingo.framework.remoting.exchange.support.header;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.Parameters;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.NamedThreadFactory;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Client;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ExchangeChannel;
import com.bingo.framework.remoting.exchange.ExchangeClient;
import com.bingo.framework.remoting.exchange.ExchangeHandler;
import com.bingo.framework.remoting.exchange.ResponseFuture;

/**
 * DefaultMessageClient
 * 
 * @author william.liangf
 * @author chao.liuc
 */
public class HeaderExchangeClient implements ExchangeClient {

    private static final Logger logger = LoggerFactory.getLogger( HeaderExchangeClient.class );

    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("bingo-remoting-client-heartbeat", true));

    // 心跳定时器
    private ScheduledFuture<?> heatbeatTimer;

    // 心跳超时，毫秒。缺省0，不会执行心跳。
    private int heartbeat;

    private int heartbeatTimeout;
    
    private final Client client;

    private final ExchangeChannel channel;

    public HeaderExchangeClient(Client client){
        if (client == null) {
            throw new IllegalArgumentException("client == null");
        }
        this.client = client;
        this.channel = new HeaderExchangeChannel(client);
        String bingo = client.getUrl().getParameter(Constants.BINGO_VERSION_KEY);
        this.heartbeat = client.getUrl().getParameter( Constants.HEARTBEAT_KEY, bingo != null && bingo.startsWith("1.0.") ? Constants.DEFAULT_HEARTBEAT : 0 );
        this.heartbeatTimeout = client.getUrl().getParameter( Constants.HEARTBEAT_TIMEOUT_KEY, heartbeat * 3 );
        if ( heartbeatTimeout < heartbeat * 2 ) {
            throw new IllegalStateException( "heartbeatTimeout < heartbeatInterval * 2" );
        }
        startHeatbeatTimer();
    }

    public ResponseFuture request(Object request) throws RemotingException {
        return channel.request(request);
    }

    public URL getUrl() {
        return channel.getUrl();
    }

    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        return channel.request(request, timeout);
    }

    public ChannelHandler getChannelHandler() {
        return channel.getChannelHandler();
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    public ExchangeHandler getExchangeHandler() {
        return channel.getExchangeHandler();
    }
    
    public void send(Object message) throws RemotingException {
        channel.send(message);
    }
    
    public void send(Object message, boolean sent) throws RemotingException {
        channel.send(message, sent);
    }

    public boolean isClosed() {
        return channel.isClosed();
    }

    public void close() {
        doClose();
        channel.close();
    }

    public void close(int timeout) {
        doClose();
        channel.close(timeout);
    }

    public void reset(URL url) {
        client.reset(url);
    }
    
    @Deprecated
    public void reset(Parameters parameters){
        reset(getUrl().addParameters(parameters.getParameters()));
    }

    public void reconnect() throws RemotingException {
        client.reconnect();
    }

    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
    }

    private void startHeatbeatTimer() {
        stopHeartbeatTimer();
        if ( heartbeat > 0 ) {
            heatbeatTimer = scheduled.scheduleWithFixedDelay(
                    new HeartBeatTask( new HeartBeatTask.ChannelProvider() {
                        public Collection<Channel> getChannels() {
                            return Collections.<Channel>singletonList( HeaderExchangeClient.this );
                        }
                    }, heartbeat, heartbeatTimeout),
                    heartbeat, heartbeat, TimeUnit.MILLISECONDS );
        }
    }

    private void stopHeartbeatTimer() {
        if (heatbeatTimer != null && ! heatbeatTimer.isCancelled()) {
            try {
                heatbeatTimer.cancel(true);
                scheduled.purge();
            } catch ( Throwable e ) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        heatbeatTimer =null;
    }

    private void doClose() {
        stopHeartbeatTimer();
    }

	@Override
	public String toString() {
		return "HeaderExchangeClient [channel=" + channel + "]";
	}
}