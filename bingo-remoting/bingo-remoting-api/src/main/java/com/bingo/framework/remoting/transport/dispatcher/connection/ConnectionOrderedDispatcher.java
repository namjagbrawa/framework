package com.bingo.framework.remoting.transport.dispatcher.connection;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Dispatcher;

/**
 * connect disconnect 保证顺序.
 * 
 * @author chao.liuc
 */
public class ConnectionOrderedDispatcher implements Dispatcher {

    public static final String NAME = "connection";

    public ChannelHandler dispatch(ChannelHandler handler, URL url) {
        return new ConnectionOrderedChannelHandler(handler, url);
    }

}