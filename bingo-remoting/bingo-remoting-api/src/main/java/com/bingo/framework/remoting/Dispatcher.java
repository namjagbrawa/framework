package com.bingo.framework.remoting;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;
import com.bingo.framework.remoting.transport.dispatcher.all.AllDispatcher;

/**
 * ChannelHandlerWrapper (SPI, Singleton, ThreadSafe)
 * 
 * @author chao.liuc
 */
@SPI(AllDispatcher.NAME)
public interface Dispatcher {

    /**
     * dispatch the message to threadpool.
     * 
     * @param handler
     * @param url
     * @return channel handler
     */
    @Adaptive({Constants.DISPATCHER_KEY, "dispather", "channel.handler"}) // 后两个参数为兼容旧配置
    ChannelHandler dispatch(ChannelHandler handler, URL url);

}