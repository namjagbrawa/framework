package com.bingo.framework.remoting.transport;

import java.net.InetSocketAddress;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;

/**
 * ChannelDelegate
 * 
 * @author william.liangf
 */
public class ChannelDelegate implements Channel {
    
    private transient Channel channel;
    
    public ChannelDelegate() {
    }

    public ChannelDelegate(Channel channel) {
        setChannel(channel);
    }
    
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == null");
        }
        this.channel = channel;
    }

    public URL getUrl() {
        return channel.getUrl();
    }

    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
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

    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
    }

    public void send(Object message) throws RemotingException {
        channel.send(message);
    }

    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    public void send(Object message, boolean sent) throws RemotingException {
        channel.send(message, sent);
    }

    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    public void close() {
        channel.close();
    }
    public void close(int timeout) {
        channel.close(timeout);
    }

    public boolean isClosed() {
        return channel.isClosed();
    }

}