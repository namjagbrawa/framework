package com.bingo.framework.remoting.exchange.support;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.bingo.framework.common.Parameters;
import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ExchangeChannel;
import com.bingo.framework.remoting.exchange.ExchangeServer;

/**
 * ExchangeServerDelegate
 * 
 * @author william.liangf
 */
public class ExchangeServerDelegate implements ExchangeServer {
    
    private transient ExchangeServer server;
    
    public ExchangeServerDelegate() {
    }

    public ExchangeServerDelegate(ExchangeServer server){
        setServer(server);
    }

    public ExchangeServer getServer() {
        return server;
    }
    
    public void setServer(ExchangeServer server) {
        this.server = server;
    }

    public boolean isBound() {
        return server.isBound();
    }

    public void reset(URL url) {
        server.reset(url);
    }

    @Deprecated
    public void reset(Parameters parameters){
        reset(getUrl().addParameters(parameters.getParameters()));
    }
    
    public Collection<Channel> getChannels() {
        return server.getChannels();
    }

    public Channel getChannel(InetSocketAddress remoteAddress) {
        return server.getChannel(remoteAddress);
    }

    public URL getUrl() {
        return server.getUrl();
    }

    public ChannelHandler getChannelHandler() {
        return server.getChannelHandler();
    }

    public InetSocketAddress getLocalAddress() {
        return server.getLocalAddress();
    }

    public void send(Object message) throws RemotingException {
        server.send(message);
    }

    public void send(Object message, boolean sent) throws RemotingException {
        server.send(message, sent);
    }

    public void close() {
        server.close();
    }

    public boolean isClosed() {
        return server.isClosed();
    }

    public Collection<ExchangeChannel> getExchangeChannels() {
        return server.getExchangeChannels();
    }

    public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress) {
        return server.getExchangeChannel(remoteAddress);
    }

    public void close(int timeout) {
        server.close(timeout);
    }

}