package com.bingo.framework.rpc.filter;

import java.util.Map;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.utils.ConfigUtils;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcContext;
import com.bingo.framework.rpc.RpcException;

/**
 * TokenInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.PROVIDER, value = Constants.TOKEN_KEY)
public class TokenFilter implements Filter {

	public Result invoke(Invoker<?> invoker, Invocation inv)
			throws RpcException {
	    String token = invoker.getUrl().getParameter(Constants.TOKEN_KEY);
	    if (ConfigUtils.isNotEmpty(token)) {
	        Class<?> serviceType = invoker.getInterface();
	        Map<String, String> attachments = inv.getAttachments();
    		String remoteToken = attachments == null ? null : attachments.get(Constants.TOKEN_KEY);
    		if (! token.equals(remoteToken)) {
    			throw new RpcException("Invalid token! Forbid invoke remote service " + serviceType + " method " + inv.getMethodName() + "() from consumer " + RpcContext.getContext().getRemoteHost() + " to provider "  + RpcContext.getContext().getLocalHost());
    		}
	    }
		return invoker.invoke(inv);
	}

}