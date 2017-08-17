package com.bingo.framework.rpc.listener;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.InvokerListener;
import com.bingo.framework.rpc.RpcException;

/**
 * InvokerListenerAdapter
 * 
 * @author william.liangf
 */
public abstract class InvokerListenerAdapter implements InvokerListener {

    public void referred(Invoker<?> invoker) throws RpcException {
    }

    public void destroyed(Invoker<?> invoker) {
    }

}