package com.bingo.framework.remoting.transport.dispatcher.message;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Dispatcher;

/**
 * 只有message receive使用线程池.
 * 
 * @author chao.liuc
 */
public class MessageOnlyDispatcher implements Dispatcher {

    public static final String NAME = "message";

    public ChannelHandler dispatch(ChannelHandler handler, URL url) {
        return new MessageOnlyChannelHandler(handler, url);
    }

}