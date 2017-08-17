package com.bingo.framework.rpc.filter;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcStatus;

/**
 * ThreadLimitInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.PROVIDER, value = Constants.EXECUTES_KEY)
public class ExecuteLimitFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        String methodName = invocation.getMethodName();
        int max = url.getMethodParameter(methodName, Constants.EXECUTES_KEY, 0);
        if (max > 0) {
            RpcStatus count = RpcStatus.getStatus(url, invocation.getMethodName());
            if (count.getActive() >= max) {
                throw new RpcException("Failed to invoke method " + invocation.getMethodName() + " in provider " + url + ", cause: The service using threads greater than <bingo:service executes=\"" + max + "\" /> limited.");
            }
        }
        long begin = System.currentTimeMillis();
        boolean isException = false;
        RpcStatus.beginCount(url, methodName);
        try {
            Result result = invoker.invoke(invocation);
            return result;
        } catch (Throwable t) {
            isException = true;
            if(t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            else {
                throw new RpcException("unexpected exception when ExecuteLimitFilter", t);
            }
        }
        finally {
            RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, isException);
        }
    }

}