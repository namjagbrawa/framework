package com.bingo.framework.remoting.transport.dispatcher;


import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.ExtensionLoader;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Dispatcher;
import com.bingo.framework.remoting.exchange.support.header.HeartbeatHandler;
import com.bingo.framework.remoting.transport.MultiMessageHandler;

/**
 * @author chao.liuc
 *
 */
public class ChannelHandlers {

    public static ChannelHandler wrap(ChannelHandler handler, URL url){
        return ChannelHandlers.getInstance().wrapInternal(handler, url);
    }

    protected ChannelHandlers() {}

    protected ChannelHandler wrapInternal(ChannelHandler handler, URL url) {
        return new MultiMessageHandler(new HeartbeatHandler(ExtensionLoader.getExtensionLoader(Dispatcher.class)
                                        .getAdaptiveExtension().dispatch(handler, url)));
    }

    private static ChannelHandlers INSTANCE = new ChannelHandlers();

    protected static ChannelHandlers getInstance() {
        return INSTANCE;
    }

    static void setTestingChannelHandlers(ChannelHandlers instance) {
        INSTANCE = instance;
    }
}