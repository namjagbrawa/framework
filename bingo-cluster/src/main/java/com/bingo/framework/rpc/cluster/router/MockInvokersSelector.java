
package com.bingo.framework.rpc.cluster.router;

import java.util.ArrayList;
import java.util.List;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Router;

/**
 * mock invoker选择器
 * @author chao.liuc
 *
 */
public class MockInvokersSelector implements Router {

	public <T> List<Invoker<T>> route(final List<Invoker<T>> invokers,
			URL url, final Invocation invocation) throws RpcException {
		if (invocation.getAttachments() == null) {
			return getNormalInvokers(invokers);
		} else {
			String value = invocation.getAttachments().get(Constants.INVOCATION_NEED_MOCK);
			if (value == null) 
				return getNormalInvokers(invokers);
			else if (Boolean.TRUE.toString().equalsIgnoreCase(value)){
				return getMockedInvokers(invokers);
			} 
		}
		return invokers;
	}
	
	private <T> List<Invoker<T>> getMockedInvokers(final List<Invoker<T>> invokers) {
		if (! hasMockProviders(invokers)){
			return null;
		}
		List<Invoker<T>> sInvokers = new ArrayList<Invoker<T>>(1);
		for (Invoker<T> invoker : invokers){
			if (invoker.getUrl().getProtocol().equals(Constants.MOCK_PROTOCOL)){
				sInvokers.add(invoker);
			}
		}
		return sInvokers;
	}
	
	private <T> List<Invoker<T>> getNormalInvokers(final List<Invoker<T>> invokers){
		if (! hasMockProviders(invokers)){
			return invokers;
		} else {
			List<Invoker<T>> sInvokers = new ArrayList<Invoker<T>>(invokers.size());
			for (Invoker<T> invoker : invokers){
				if (! invoker.getUrl().getProtocol().equals(Constants.MOCK_PROTOCOL)){
					sInvokers.add(invoker);
				}
			}
			return sInvokers;
		}
	}
	
	private <T> boolean hasMockProviders(final List<Invoker<T>> invokers){
		boolean hasMockProvider = false;
		for (Invoker<T> invoker : invokers){
			if (invoker.getUrl().getProtocol().equals(Constants.MOCK_PROTOCOL)){
				hasMockProvider = true;
				break;
			}
		}
		return hasMockProvider;
	}

    public URL getUrl() {
        return null;
    }

    public int compareTo(Router o) {
        return 1;
    }

}
