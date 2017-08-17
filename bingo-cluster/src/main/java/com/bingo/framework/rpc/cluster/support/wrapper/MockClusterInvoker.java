
package com.bingo.framework.rpc.cluster.support.wrapper;

import java.util.List;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcInvocation;
import com.bingo.framework.rpc.RpcResult;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.support.MockInvoker;

/**
 * @author chao.liuc
 */
public class MockClusterInvoker<T> implements Invoker<T>{
	
	private static final Logger logger = LoggerFactory.getLogger(MockClusterInvoker.class);

	private final Directory<T> directory ;
	
	private final Invoker<T> invoker;

    public MockClusterInvoker(Directory<T> directory, Invoker<T> invoker) {
       	this.directory = directory;
       	this.invoker = invoker;
    }

	public URL getUrl() {
		return directory.getUrl();
	}

	public boolean isAvailable() {
		return directory.isAvailable();
	}

	public void destroy() {
		this.invoker.destroy();
	}

	public Class<T> getInterface() {
		return directory.getInterface();
	}

	public Result invoke(Invocation invocation) throws RpcException {
		Result result = null;
        
        String value = directory.getUrl().getMethodParameter(invocation.getMethodName(), Constants.MOCK_KEY, Boolean.FALSE.toString()).trim(); 
        if (value.length() == 0 || value.equalsIgnoreCase("false")){
        	//no mock
        	result = this.invoker.invoke(invocation);
        } else if (value.startsWith("force")) {
        	if (logger.isWarnEnabled()) {
        		logger.info("force-mock: " + invocation.getMethodName() + " force-mock enabled , url : " +  directory.getUrl());
        	}
        	//force:direct mock
        	result = doMockInvoke(invocation, null);
        } else {
        	//fail-mock
        	try {
        		result = this.invoker.invoke(invocation);
        	}catch (RpcException e) {
				if (e.isBiz()) {
					throw e;
				} else {
					if (logger.isWarnEnabled()) {
		        		logger.info("fail-mock: " + invocation.getMethodName() + " fail-mock enabled , url : " +  directory.getUrl(), e);
		        	}
					result = doMockInvoke(invocation, e);
				}
			}
        }
        return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Result doMockInvoke(Invocation invocation,RpcException e){
		Result result = null;
    	Invoker<T> minvoker ;
    	
    	List<Invoker<T>> mockInvokers = selectMockInvoker(invocation);
		if (mockInvokers == null || mockInvokers.size() == 0){
			minvoker = (Invoker<T>) new MockInvoker(directory.getUrl());
		} else {
			minvoker = mockInvokers.get(0);
		}
		try {
			result = minvoker.invoke(invocation);
		} catch (RpcException me) {
			if (me.isBiz()) {
				result = new RpcResult(me.getCause());
			} else {
				throw new RpcException(me.getCode(), getMockExceptionMessage(e, me), me.getCause());
			}
//			
		} catch (Throwable me) {
			throw new RpcException(getMockExceptionMessage(e, me), me.getCause());
		}
		return result;
    }
	
	private String getMockExceptionMessage(Throwable t, Throwable mt){
		String msg = "mock error : " + mt.getMessage();
		if (t != null){
			msg = msg + ", invoke error is :" + StringUtils.toString(t);
		}
		return msg;
	}

	/**
     * 返回MockInvoker
     * 契约：
     * directory根据invocation中是否有Constants.INVOCATION_NEED_MOCK，来判断获取的是一个normal invoker 还是一个 mock invoker
     * 如果directorylist 返回多个mock invoker，只使用第一个invoker.
     * @param invocation
     * @return 
     */
    private List<Invoker<T>> selectMockInvoker(Invocation invocation){
    	//TODO generic invoker？
        if (invocation instanceof RpcInvocation){
            //存在隐含契约(虽然在接口声明中增加描述，但扩展性会存在问题.同时放在attachement中的做法需要改进
        	((RpcInvocation)invocation).setAttachment(Constants.INVOCATION_NEED_MOCK, Boolean.TRUE.toString());
            //directory根据invocation中attachment是否有Constants.INVOCATION_NEED_MOCK，来判断获取的是normal invokers or mock invokers
        	List<Invoker<T>> invokers = directory.list(invocation);
            return invokers;
        } else {
            return null ;
        }
    }

	@Override
	public String toString() {
		return "invoker :" + this.invoker + ",directory: " + this.directory;
	}
}