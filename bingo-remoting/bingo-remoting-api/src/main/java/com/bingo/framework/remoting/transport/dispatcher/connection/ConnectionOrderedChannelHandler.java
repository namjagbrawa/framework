package com.bingo.framework.remoting.transport.dispatcher.connection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.threadpool.support.AbortPolicyWithReport;
import com.bingo.framework.common.utils.NamedThreadFactory;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.ExecutionException;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.transport.dispatcher.ChannelEventRunnable;
import com.bingo.framework.remoting.transport.dispatcher.WrappedChannelHandler;
import com.bingo.framework.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState;

public class ConnectionOrderedChannelHandler extends WrappedChannelHandler {

    protected final ThreadPoolExecutor connectionExecutor;
    private final int queuewarninglimit ;
    
    public ConnectionOrderedChannelHandler(ChannelHandler handler, URL url) {
        super(handler, url);
        String threadName = url.getParameter(Constants.THREAD_NAME_KEY,Constants.DEFAULT_THREAD_NAME);
        connectionExecutor = new ThreadPoolExecutor(1, 1,
                                     0L, TimeUnit.MILLISECONDS,
                                     new LinkedBlockingQueue<Runnable>(url.getPositiveParameter(Constants.CONNECT_QUEUE_CAPACITY, Integer.MAX_VALUE)),
                                     new NamedThreadFactory(threadName, true),
                                     new AbortPolicyWithReport(threadName, url)
            );  // FIXME 没有地方释放connectionExecutor！
        queuewarninglimit = url.getParameter(Constants.CONNECT_QUEUE_WARNING_SIZE, Constants.DEFAULT_CONNECT_QUEUE_WARNING_SIZE);
    }

    public void connected(Channel channel) throws RemotingException {
        try{
            checkQueueLength();
            connectionExecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CONNECTED));
        }catch (Throwable t) {
            throw new ExecutionException("connect event", channel, getClass()+" error when process connected event ." , t);
        }
    }

    public void disconnected(Channel channel) throws RemotingException {
        try{
            checkQueueLength();
            connectionExecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.DISCONNECTED));
        }catch (Throwable t) {
            throw new ExecutionException("disconnected event", channel, getClass()+" error when process disconnected event ." , t);
        }
    }

    public void received(Channel channel, Object message) throws RemotingException {
        ExecutorService cexecutor = executor;
        if (cexecutor == null || cexecutor.isShutdown()) {
            cexecutor = SHARED_EXECUTOR;
        }
        try {
            cexecutor.execute(new ChannelEventRunnable(channel, handler, ChannelState.RECEIVED, message));
        } catch (Throwable t) {
            throw new ExecutionException(message, channel, getClass() + " error when process received event .", t);
        }
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        ExecutorService cexecutor = executor;
        if (cexecutor == null || cexecutor.isShutdown()) { 
            cexecutor = SHARED_EXECUTOR;
        } 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CAUGHT, exception));
        }catch (Throwable t) {
            throw new ExecutionException("caught event", channel, getClass()+" error when process caught event ." , t);
        }
    }
    
    private void checkQueueLength(){
        if (connectionExecutor.getQueue().size() > queuewarninglimit){
            logger.warn(new IllegalThreadStateException("connectionordered channel handler `queue size: "+connectionExecutor.getQueue().size()+" exceed the warning limit number :"+queuewarninglimit));
        }
    }
}