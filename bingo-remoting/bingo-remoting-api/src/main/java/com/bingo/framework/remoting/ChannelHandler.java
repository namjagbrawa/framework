package com.bingo.framework.remoting;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.SPI;


/**
 * ChannelHandler. (API, Prototype, ThreadSafe)
 * 
 * @see Transporter#bind(URL, ChannelHandler)
 * @see Transporter#connect(URL, ChannelHandler)
 * @author qian.lei
 * @author william.liangf
 */
@SPI
public interface ChannelHandler {

    /**
     * on channel connected.
     * 
     * @param channel channel.
     */
    void connected(Channel channel) throws RemotingException;

    /**
     * on channel disconnected.
     * 
     * @param channel channel.
     */
    void disconnected(Channel channel) throws RemotingException;

    /**
     * on message sent.
     * 
     * @param channel channel.
     * @param message message.
     */
    void sent(Channel channel, Object message) throws RemotingException;

    /**
     * on message received.
     * 
     * @param channel channel.
     * @param message message.
     */
    void received(Channel channel, Object message) throws RemotingException;

    /**
     * on exception caught.
     * 
     * @param channel channel.
     * @param exception exception.
     */
    void caught(Channel channel, Throwable exception) throws RemotingException;

}