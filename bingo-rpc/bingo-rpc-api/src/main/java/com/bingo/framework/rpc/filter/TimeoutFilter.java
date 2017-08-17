package com.bingo.framework.rpc.filter;

import java.util.Arrays;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;

/**
 * 如果执行timeout，则log记录下，不干涉服务的运行
 * 
 * @author chao.liuc
 */
@Activate(group = Constants.PROVIDER)
public class TimeoutFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TimeoutFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long start = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        long elapsed = System.currentTimeMillis() - start;
        if (invoker.getUrl() != null
                && elapsed > invoker.getUrl().getMethodParameter(invocation.getMethodName(),
                        "timeout", Integer.MAX_VALUE)) {
            if (logger.isWarnEnabled()) {
                logger.warn("invoke time out. method: " + invocation.getMethodName()
                        + "arguments: " + Arrays.toString(invocation.getArguments()) + " , url is "
                        + invoker.getUrl() + ", invoke elapsed " + elapsed + " ms.");
            }
        }
        return result;
    }
    
}