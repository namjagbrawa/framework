
package com.bingo.framework.rpc.cluster.support;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
public class MergeableCluster implements Cluster {

    public static final String NAME = "mergeable";

    public <T> Invoker<T> join( Directory<T> directory ) throws RpcException {
        return new MergeableClusterInvoker<T>( directory );
    }

}
