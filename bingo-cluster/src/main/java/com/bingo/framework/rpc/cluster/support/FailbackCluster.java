
package com.bingo.framework.rpc.cluster.support;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * 失败自动恢复，后台记录失败请求，定时重发，通常用于消息通知操作。
 * 
 * <a href="http://en.wikipedia.org/wiki/Failback">Failback</a>
 * 
 * @author william.liangf
 */
public class FailbackCluster implements Cluster {

    public final static String NAME = "failback";    

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailbackClusterInvoker<T>(directory);
    }

}