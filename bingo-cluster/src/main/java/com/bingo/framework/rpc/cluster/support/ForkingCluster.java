
package com.bingo.framework.rpc.cluster.support;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * 并行调用，只要一个成功即返回，通常用于实时性要求较高的操作，但需要浪费更多服务资源。
 * 
 * <a href="http://en.wikipedia.org/wiki/Fork_(topology)">Fork</a>
 * 
 * @author william.liangf
 */
public class ForkingCluster implements Cluster {
    
    public final static String NAME = "forking"; 
    
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new ForkingClusterInvoker<T>(directory);
    }

}