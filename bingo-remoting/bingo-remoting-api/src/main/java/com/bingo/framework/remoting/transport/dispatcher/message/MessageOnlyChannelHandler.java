package com.bingo.framework.remoting.transport.dispatcher.message;

import java.util.concurrent.ExecutorService;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.ExecutionException;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.transport.dispatcher.ChannelEventRunnable;
import com.bingo.framework.remoting.transport.dispatcher.WrappedChannelHandler;

public class MessageOnlyChannelHandler extends WrappedChannelHandler {
    
    public MessageOnlyChannelHandler(ChannelHandler handler, URL url) {
        super(handler, url);
    }

    public void received(Channel channel, Object message) throws RemotingException {
        ExecutorService cexecutor = executor;
        if (cexecutor == null || cexecutor.isShutdown()) {
            cexecutor = SHARED_EXECUTOR;
        }
        try {
            cexecutor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.RECEIVED, message));
        } catch (Throwable t) {
            throw new ExecutionException(message, channel, getClass() + " error when process received event .", t);
        }
    }

}