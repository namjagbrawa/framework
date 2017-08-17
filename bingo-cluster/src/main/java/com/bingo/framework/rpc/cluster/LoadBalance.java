
package com.bingo.framework.rpc.cluster;

import java.util.List;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.loadbalance.RandomLoadBalance;

/**
 * LoadBalance. (SPI, Singleton, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Load_balancing_(computing)">Load-Balancing</a>
 * 
 * @see Cluster#join(Directory)
 * @author qian.lei
 * @author william.liangf
 */
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {

	/**
	 * select one invoker in list.
	 * 
	 * @param invokers invokers.
	 * @param url refer url
	 * @param invocation invocation.
	 * @return selected invoker.
	 */
    @Adaptive("loadbalance")
	<T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;

}