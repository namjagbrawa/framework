package com.bingo.framework.remoting.transport;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;

/**
 * AbstractChannel
 * 
 * @author william.liangf
 */
public abstract class AbstractChannel extends AbstractPeer implements Channel {

    public AbstractChannel(URL url, ChannelHandler handler){
        super(url, handler);
    }

    public void send(Object message, boolean sent) throws RemotingException {
        if (isClosed()) {
            throw new RemotingException(this, "Failed to send message "
                                              + (message == null ? "" : message.getClass().getName()) + ":" + message
                                              + ", cause: Channel closed. channel: " + getLocalAddress() + " -> " + getRemoteAddress());
        }
    }

    @Override
    public String toString() {
        return getLocalAddress() + " -> " + getRemoteAddress();
    }

}