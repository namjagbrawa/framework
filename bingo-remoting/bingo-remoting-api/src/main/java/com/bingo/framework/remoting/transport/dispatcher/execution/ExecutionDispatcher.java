package com.bingo.framework.remoting.transport.dispatcher.execution;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Dispatcher;

/**
 * 除发送全部使用线程池处理
 * 
 * @author chao.liuc
 */
public class ExecutionDispatcher implements Dispatcher {
    
    public static final String NAME = "execution";

    public ChannelHandler dispatch(ChannelHandler handler, URL url) {
        return new ExecutionChannelHandler(handler, url);
    }

}