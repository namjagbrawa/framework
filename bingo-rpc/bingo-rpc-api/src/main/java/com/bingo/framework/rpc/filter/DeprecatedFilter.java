package com.bingo.framework.rpc.filter;

import java.util.Set;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.ConcurrentHashSet;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;

/**
 * DeprecatedInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.CONSUMER, value = Constants.DEPRECATED_KEY)
public class DeprecatedFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeprecatedFilter.class);

    private static final Set<String> logged = new ConcurrentHashSet<String>();

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String key = invoker.getInterface().getName() + "." + invocation.getMethodName();
        if (! logged.contains(key)) {
            logged.add(key);
            if (invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.DEPRECATED_KEY, false)) {
                LOGGER.error("The service method " + invoker.getInterface().getName() + "." + getMethodSignature(invocation) + " is DEPRECATED! Declare from " + invoker.getUrl());
            }
        }
        return invoker.invoke(invocation);
    }
    
    private String getMethodSignature(Invocation invocation) {
        StringBuilder buf = new StringBuilder(invocation.getMethodName());
        buf.append("(");
        Class<?>[] types = invocation.getParameterTypes();
        if (types != null && types.length > 0) {
            boolean first = true;
            for (Class<?> type : types) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(type.getSimpleName());
            }
        }
        buf.append(")");
        return buf.toString();
    }

}