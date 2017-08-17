package com.bingo.framework.remoting.transport;

import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;

/**
 * ChannelHandlerAdapter.
 * 
 * @author qian.lei
 */
public class ChannelHandlerAdapter implements ChannelHandler {

    public void connected(Channel channel) throws RemotingException {
    }

    public void disconnected(Channel channel) throws RemotingException {
    }

    public void sent(Channel channel, Object message) throws RemotingException {
    }

    public void received(Channel channel, Object message) throws RemotingException {
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
    }

}