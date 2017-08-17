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
 * LimitInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.CONSUMER, value = Constants.ACTIVES_KEY)
public class ActiveLimitFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        String methodName = invocation.getMethodName();
        int max = invoker.getUrl().getMethodParameter(methodName, Constants.ACTIVES_KEY, 0);
        RpcStatus count = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName());
        if (max > 0) {
            long timeout = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.TIMEOUT_KEY, 0);
            long start = System.currentTimeMillis();
            long remain = timeout;
            int active = count.getActive();
            if (active >= max) {
                synchronized (count) {
                    while ((active = count.getActive()) >= max) {
                        try {
                            count.wait(remain);
                        } catch (InterruptedException e) {
                        }
                        long elapsed = System.currentTimeMillis() - start;
                        remain = timeout - elapsed;
                        if (remain <= 0) {
                            throw new RpcException("Waiting concurrent invoke timeout in client-side for service:  "
                                                   + invoker.getInterface().getName() + ", method: "
                                                   + invocation.getMethodName() + ", elapsed: " + elapsed
                                                   + ", timeout: " + timeout + ". concurrent invokes: " + active
                                                   + ". max concurrent invoke limit: " + max);
                        }
                    }
                }
            }
        }
        try {
            long begin = System.currentTimeMillis();
            RpcStatus.beginCount(url, methodName);
            try {
                Result result = invoker.invoke(invocation);
                RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, true);
                return result;
            } catch (RuntimeException t) {
                RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, false);
                throw t;
            }
        } finally {
            if(max>0){
                synchronized (count) {
                    count.notify();
                } 
            }
        }
    }

}