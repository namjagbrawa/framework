
package com.bingo.framework.rpc.cluster.support;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * 失败安全，出现异常时，直接忽略，通常用于写入审计日志等操作。 
 * 
 * <a href="http://en.wikipedia.org/wiki/Fail-safe">Fail-safe</a>
 * 
 * @author william.liangf
 */
public class FailsafeCluster implements Cluster {

    public final static String NAME = "failsafe";

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailsafeClusterInvoker<T>(directory);
    }

}