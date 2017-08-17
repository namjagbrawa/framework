
package com.bingo.framework.rpc.cluster.support;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * 快速失败，只发起一次调用，失败立即报错，通常用于非幂等性的写操作。
 *  
 * <a href="http://en.wikipedia.org/wiki/Fail-fast">Fail-fast</a>
 * 
 * @author william.liangf
 */
public class FailfastCluster implements Cluster {

    public final static String NAME = "failfast";

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailfastClusterInvoker<T>(directory);
    }

}