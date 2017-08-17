package com.bingo.framework.remoting.exchange;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.bingo.framework.remoting.Server;

/**
 * ExchangeServer. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author william.liangf
 */
public interface ExchangeServer extends Server {

    /**
     * get channels.
     * 
     * @return channels
     */
    Collection<ExchangeChannel> getExchangeChannels();

    /**
     * get channel.
     * 
     * @param remoteAddress
     * @return channel
     */
    ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress);

}