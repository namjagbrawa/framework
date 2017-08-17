package com.bingo.framework.remoting.transport.dispatcher.all;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Dispatcher;

/**
 * 默认的线程池配置
 * 
 * @author chao.liuc
 */
public class AllDispatcher implements Dispatcher {
    
    public static final String NAME = "all";

    public ChannelHandler dispatch(ChannelHandler handler, URL url) {
        return new AllChannelHandler(handler, url);
    }

}