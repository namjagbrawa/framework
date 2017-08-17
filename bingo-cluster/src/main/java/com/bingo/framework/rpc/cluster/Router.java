
package com.bingo.framework.rpc.cluster;

import java.util.List;

import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;

/**
 * Router. (SPI, Prototype, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Routing">Routing</a>
 * 
 * @see Cluster#join(Directory)
 * @see Directory#list(Invocation)
 * @author chao.liuc
 */
public interface Router extends Comparable<Router> {

    /**
     * get the router url.
     * 
     * @return url
     */
    URL getUrl();

    /**
     * route.
     * 
     * @param invokers
     * @param url refer url
     * @param invocation
     * @return routed invokers
     * @throws RpcException
     */
	<T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;

}