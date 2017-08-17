package com.bingo.framework.remoting.exchange;

import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.RemotingException;

/**
 * ExchangeChannel. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author william.liangf
 */
public interface ExchangeChannel extends Channel {

    /**
     * send request.
     * 
     * @param request
     * @return response future
     * @throws RemotingException
     */
    ResponseFuture request(Object request) throws RemotingException;

    /**
     * send request.
     * 
     * @param request
     * @param timeout
     * @return response future
     * @throws RemotingException
     */
    ResponseFuture request(Object request, int timeout) throws RemotingException;

    /**
     * get message handler.
     * 
     * @return message handler
     */
    ExchangeHandler getExchangeHandler();

    /**
     * graceful close.
     * 
     * @param timeout
     */
    void close(int timeout);

}