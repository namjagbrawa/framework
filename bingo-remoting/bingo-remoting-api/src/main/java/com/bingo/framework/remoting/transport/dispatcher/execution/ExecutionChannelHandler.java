package com.bingo.framework.remoting.transport.dispatcher.execution;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.transport.dispatcher.ChannelEventRunnable;
import com.bingo.framework.remoting.transport.dispatcher.WrappedChannelHandler;

public class ExecutionChannelHandler extends WrappedChannelHandler {
    
    public ExecutionChannelHandler(ChannelHandler handler, URL url) {
        super(handler, url);
    }

    public void connected(Channel channel) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler , ChannelEventRunnable.ChannelState.CONNECTED));
    }

    public void disconnected(Channel channel) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler , ChannelEventRunnable.ChannelState.DISCONNECTED));
    }

    public void received(Channel channel, Object message) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.RECEIVED, message));
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler , ChannelEventRunnable.ChannelState.CAUGHT, exception));
    }

}