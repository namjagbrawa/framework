package com.bingo.framework.remoting.exchange;

import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.telnet.TelnetHandler;

/**
 * ExchangeHandler. (API, Prototype, ThreadSafe)
 * 
 * @author william.liangf
 */
public interface ExchangeHandler extends ChannelHandler, TelnetHandler {

    /**
     * reply.
     * 
     * @param channel
     * @param request
     * @return response
     * @throws RemotingException
     */
    Object reply(ExchangeChannel channel, Object request) throws RemotingException;

}