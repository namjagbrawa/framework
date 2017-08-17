package com.bingo.framework.remoting.transport;

import com.bingo.framework.remoting.ChannelHandler;

/**
 * @author chao.liuc
 */
public interface ChannelHandlerDelegate extends ChannelHandler {
    public ChannelHandler getHandler();
}