package com.bingo.framework.remoting.exchange;

import com.bingo.framework.remoting.RemotingException;

/**
 * Future. (API/SPI, Prototype, ThreadSafe)
 * 
 * @see ExchangeChannel#request(Object)
 * @see ExchangeChannel#request(Object, int)
 * @author qian.lei
 * @author william.liangf
 */
public interface ResponseFuture {

    /**
     * get result.
     * 
     * @return result.
     */
    Object get() throws RemotingException;

    /**
     * get result with the specified timeout.
     * 
     * @param timeoutInMillis timeout.
     * @return result.
     */
    Object get(int timeoutInMillis) throws RemotingException;

    /**
     * set callback.
     * 
     * @param callback
     */
    void setCallback(ResponseCallback callback);

    /**
     * check is done.
     * 
     * @return done or not.
     */
    boolean isDone();

}