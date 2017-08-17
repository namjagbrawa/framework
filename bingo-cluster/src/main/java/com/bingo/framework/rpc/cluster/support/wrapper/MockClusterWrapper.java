
package com.bingo.framework.rpc.cluster.support.wrapper;

import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Cluster;
import com.bingo.framework.rpc.cluster.Directory;

/**
 * mock impl
 * 
 * @author chao.liuc
 * 
 */
public class MockClusterWrapper implements Cluster {

	private Cluster cluster;

	public MockClusterWrapper(Cluster cluster) {
		this.cluster = cluster;
	}

	public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
		return new MockClusterInvoker<T>(directory,
				this.cluster.join(directory));
	}

}
