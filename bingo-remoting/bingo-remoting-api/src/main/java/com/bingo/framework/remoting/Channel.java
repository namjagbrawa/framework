package com.bingo.framework.remoting;

import java.net.InetSocketAddress;

/**
 * Channel. (API/SPI, Prototype, ThreadSafe)
 * 
 * @see Client
 * @see Server#getChannels()
 * @see Server#getChannel(InetSocketAddress)
 * @author qian.lei
 * @author william.liangf
 */
public interface Channel extends Endpoint {

    /**
     * get remote address.
     * 
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * is connected.
     * 
     * @return connected
     */
    boolean isConnected();

    /**
     * has attribute.
     * 
     * @param key key.
     * @return has or has not.
     */
    boolean hasAttribute(String key);

    /**
     * get attribute.
     * 
     * @param key key.
     * @return value.
     */
    Object getAttribute(String key);

    /**
     * set attribute.
     * 
     * @param key key.
     * @param value value.
     */
    void setAttribute(String key,Object value);
    
    /**
     * remove attribute.
     * 
     * @param key key.
     */
    void removeAttribute(String key);

}