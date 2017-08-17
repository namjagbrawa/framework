
package com.bingo.framework.rpc.cluster.support;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * 失败转移，当出现失败，重试其它服务器，通常用于读操作，但重试会带来更长延迟。 
 * 
 * <a href="http://en.wikipedia.org/wiki/Failover">Failover</a>
 * 
 * @author william.liangf
 */
public class FailoverCluster implements Cluster {

    public final static String NAME = "failover";

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailoverClusterInvoker<T>(directory);
    }

}