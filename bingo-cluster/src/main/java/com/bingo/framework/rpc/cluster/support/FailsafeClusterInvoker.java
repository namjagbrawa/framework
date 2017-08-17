
package com.bingo.framework.rpc.cluster.support;

import java.util.List;

import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcResult;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.cluster.LoadBalance;

/**
 * 失败安全，出现异常时，直接忽略，通常用于写入审计日志等操作。
 * 
 * <a href="http://en.wikipedia.org/wiki/Fail-safe">Fail-safe</a>
 * 
 * @author william.liangf
 */
public class FailsafeClusterInvoker<T> extends AbstractClusterInvoker<T>{
    private static final Logger logger = LoggerFactory.getLogger(FailsafeClusterInvoker.class);
    
    public FailsafeClusterInvoker(Directory<T> directory) {
        super(directory);
    }
    
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        try {
            checkInvokers(invokers, invocation);
            Invoker<T> invoker = select(loadbalance, invocation, invokers, null);
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            logger.error("Failsafe ignore exception: " + e.getMessage(), e);
            return new RpcResult(); // ignore
        }
    }
}