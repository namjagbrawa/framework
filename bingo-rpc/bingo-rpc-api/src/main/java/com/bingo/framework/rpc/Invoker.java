package com.bingo.framework.rpc;

import com.bingo.framework.common.Node;
import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.protocol.AbstractInvoker;

/**
 * Invoker. (API/SPI, Prototype, ThreadSafe)
 * 
 * @see Protocol#refer(Class, URL)
 * @see InvokerListener
 * @see AbstractInvoker
 * @author william.liangf
 */
public interface Invoker<T> extends Node {

    /**
     * get service interface.
     * 
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     * 
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

}