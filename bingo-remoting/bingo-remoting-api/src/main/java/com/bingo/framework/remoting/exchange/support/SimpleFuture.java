package com.bingo.framework.remoting.exchange.support;

import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ResponseCallback;
import com.bingo.framework.remoting.exchange.ResponseFuture;

/**
 * SimpleFuture
 * 
 * @author william.liangf
 */
public class SimpleFuture implements ResponseFuture {
    
    private final Object value;

    public SimpleFuture(Object value){
        this.value = value;
    }

    public Object get() throws RemotingException {
        return value;
    }

    public Object get(int timeoutInMillis) throws RemotingException {
        return value;
    }

    public void setCallback(ResponseCallback callback) {
        callback.done(value);
    }

    public boolean isDone() {
        return true;
    }

}