
package com.bingo.framework.rpc.cluster;

import java.util.List;

import com.bingo.framework.common.Node;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;

/**
 * Directory. (SPI, Prototype, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Directory_service">Directory Service</a>
 * 
 * @see Cluster#join(Directory)
 * @author william.liangf
 */
public interface Directory<T> extends Node {
    
    /**
     * get service type.
     * 
     * @return service type.
     */
    Class<T> getInterface();

    /**
     * list invokers.
     * 
     * @return invokers
     */
    List<Invoker<T>> list(Invocation invocation) throws RpcException;
    
}