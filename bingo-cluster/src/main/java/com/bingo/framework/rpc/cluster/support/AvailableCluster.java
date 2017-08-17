
package com.bingo.framework.rpc.cluster.support;

import java.util.List;

import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.cluster.LoadBalance;

/**
 * AvailableCluster
 * 
 * @author william.liangf
 */
public class AvailableCluster implements Cluster {
    
    public static final String NAME = "available";

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        
        return new AbstractClusterInvoker<T>(directory) {
            public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
                for (Invoker<T> invoker : invokers) {
                    if (invoker.isAvailable()) {
                        return invoker.invoke(invocation);
                    }
                }
                throw new RpcException("No provider available in " + invokers);
            }
        };
        
    }

}