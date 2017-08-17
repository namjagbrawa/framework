package com.bingo.framework.rpc;

import com.bingo.framework.common.extension.SPI;

/**
 * Filter. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI
public interface Filter {

	/**
	 * do invoke filter.
	 * 
	 * <code>
	 * // before filter
     * Result result = invoker.invoke(invocation);
     * // after filter
     * return result;
     * </code>
     * 
     * @see Invoker#invoke(Invocation)
	 * @param invoker service
	 * @param invocation invocation.
	 * @return invoke result.
	 * @throws RpcException
	 */
	Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}