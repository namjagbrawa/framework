package com.bingo.framework.remoting.transport.dispatcher.direct;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Dispatcher;

/**
 * 不派发线程池。
 * 
 * @author chao.liuc
 */
public class DirectDispatcher implements Dispatcher {
    
    public static final String NAME = "direct";

    public ChannelHandler dispatch(ChannelHandler handler, URL url) {
        return handler;
    }

}