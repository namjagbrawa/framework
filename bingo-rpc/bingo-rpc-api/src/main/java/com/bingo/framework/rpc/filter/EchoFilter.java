package com.bingo.framework.rpc.filter;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcResult;

/**
 * EchoInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.PROVIDER, order = -110000)
public class EchoFilter implements Filter {

	public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
		if(inv.getMethodName().equals(Constants.$ECHO) && inv.getArguments() != null && inv.getArguments().length == 1 )
			return new RpcResult(inv.getArguments()[0]);
		return invoker.invoke(inv);
	}

}