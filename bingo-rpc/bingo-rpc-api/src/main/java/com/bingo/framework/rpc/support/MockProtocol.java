package com.bingo.framework.rpc.support;

import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.protocol.AbstractProtocol;

/**
 * MockProtocol 用于在consumer side 通过url及类型生成一个mockInvoker
 * @author chao.liuc
 *
 */
final public class MockProtocol extends AbstractProtocol {

	public int getDefaultPort() {
		return 0;
	}

	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		throw new UnsupportedOperationException();
	}

	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		return new MockInvoker<T>(url);
	}
}
