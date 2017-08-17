package com.bingo.framework.remoting.exchange.support;

import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ExchangeChannel;

/**
 * Replier. (API, Prototype, ThreadSafe)
 * 
 * @author william.liangf
 */
public interface Replier<T> {

    /**
     * reply.
     * 
     * @param channel
     * @param request
     * @return response
     * @throws RemotingException
     */
    Object reply(ExchangeChannel channel, T request) throws RemotingException;

}