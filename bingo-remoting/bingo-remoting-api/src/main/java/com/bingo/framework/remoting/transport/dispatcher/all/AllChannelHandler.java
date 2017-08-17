package com.bingo.framework.remoting.transport.dispatcher.all;

import java.util.concurrent.ExecutorService;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.ExecutionException;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.transport.dispatcher.ChannelEventRunnable;
import com.bingo.framework.remoting.transport.dispatcher.WrappedChannelHandler;
import com.bingo.framework.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState;

public class AllChannelHandler extends WrappedChannelHandler {
    
    public AllChannelHandler(ChannelHandler handler, URL url) {
        super(handler, url);
    }

    public void connected(Channel channel) throws RemotingException {
        ExecutorService cexecutor = getExecutorService(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CONNECTED));
        }catch (Throwable t) {
            throw new ExecutionException("connect event", channel, getClass()+" error when process connected event ." , t);
        }
    }
    
    public void disconnected(Channel channel) throws RemotingException {
        ExecutorService cexecutor = getExecutorService(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.DISCONNECTED));
        }catch (Throwable t) {
            throw new ExecutionException("disconnect event", channel, getClass()+" error when process disconnected event ." , t);
        }
    }

    public void received(Channel channel, Object message) throws RemotingException {
        ExecutorService cexecutor = getExecutorService();
        try {
            cexecutor.execute(new ChannelEventRunnable(channel, handler, ChannelState.RECEIVED, message));
        } catch (Throwable t) {
            throw new ExecutionException(message, channel, getClass() + " error when process received event .", t);
        }
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        ExecutorService cexecutor = getExecutorService(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CAUGHT, exception));
        }catch (Throwable t) {
            throw new ExecutionException("caught event", channel, getClass()+" error when process caught event ." , t);
        }
    }

    private ExecutorService getExecutorService() {
        ExecutorService cexecutor = executor;
        if (cexecutor == null || cexecutor.isShutdown()) { 
            cexecutor = SHARED_EXECUTOR;
        }
        return cexecutor;
    }
}