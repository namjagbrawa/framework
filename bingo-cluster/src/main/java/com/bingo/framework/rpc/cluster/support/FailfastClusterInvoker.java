
package com.bingo.framework.rpc.cluster.support;

import java.util.List;

import com.bingo.framework.common.Version;
import com.bingo.framework.common.utils.NetUtils;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.cluster.LoadBalance;

/**
 * 快速失败，只发起一次调用，失败立即报错，通常用于非幂等性的写操作。
 * 
 * <a href="http://en.wikipedia.org/wiki/Fail-fast">Fail-fast</a>
 * 
 * @author william.liangf
 * @author chao.liuc
 */
public class FailfastClusterInvoker<T> extends AbstractClusterInvoker<T>{

    public FailfastClusterInvoker(Directory<T> directory) {
        super(directory);
    }
    
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        checkInvokers(invokers, invocation);
        Invoker<T> invoker = select(loadbalance, invocation, invokers, null);
        try {
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            if (e instanceof RpcException && ((RpcException)e).isBiz()) { // biz exception.
                throw (RpcException) e;
            }
            throw new RpcException(e instanceof RpcException ? ((RpcException)e).getCode() : 0, "Failfast invoke providers " + invoker.getUrl() + " " + loadbalance.getClass().getSimpleName() + " select from all providers " + invokers + " for service " + getInterface().getName() + " method " + invocation.getMethodName() + " on consumer " + NetUtils.getLocalHost() + " use bingo version " + Version.getVersion() + ", but no luck to perform the invocation. Last error is: " + e.getMessage(), e.getCause() != null ? e.getCause() : e);
        }
    }
}