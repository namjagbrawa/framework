package com.bingo.framework.remoting;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.bingo.framework.common.Resetable;
import com.bingo.framework.common.Parameters;
import com.bingo.framework.common.URL;

/**
 * Remoting Server. (API/SPI, Prototype, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Client%E2%80%93server_model">Client/Server</a>
 * 
 * @see Transporter#bind(URL, ChannelHandler)
 * @author qian.lei
 */
public interface Server extends Endpoint, Resetable {
    
    /**
     * is bound.
     * 
     * @return bound
     */
    boolean isBound();

    /**
     * get channels.
     * 
     * @return channels
     */
    Collection<Channel> getChannels();

    /**
     * get channel.
     * 
     * @param remoteAddress
     * @return channel
     */
    Channel getChannel(InetSocketAddress remoteAddress);

    @Deprecated
    void reset(Parameters parameters);
    
}