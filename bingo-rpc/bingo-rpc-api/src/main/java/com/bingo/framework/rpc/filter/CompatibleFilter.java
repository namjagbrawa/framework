package com.bingo.framework.rpc.filter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.CompatibleTypeUtils;
import com.bingo.framework.common.utils.PojoUtils;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcResult;

/**
 * CompatibleFilter
 * 
 * @author william.liangf
 */
public class CompatibleFilter implements Filter {
    
    private static Logger logger = LoggerFactory.getLogger(CompatibleFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        if (! invocation.getMethodName().startsWith("$") && ! result.hasException()) {
            Object value = result.getValue();
            if (value != null) {
                try {
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Class<?> type = method.getReturnType();
                    Object newValue;
                    String serialization = invoker.getUrl().getParameter(Constants.SERIALIZATION_KEY); 
                    if ("json".equals(serialization)
                            || "fastjson".equals(serialization)){
                        Type gtype = method.getGenericReturnType();
                        newValue = PojoUtils.realize(value, type, gtype);
                    } else if (! type.isInstance(value)) {
                        newValue = PojoUtils.isPojo(type)
                            ? PojoUtils.realize(value, type) 
                            : CompatibleTypeUtils.compatibleTypeConvert(value, type);
                        
                    } else {
                        newValue = value;
                    }
                    if (newValue != value) {
                        result = new RpcResult(newValue);
                    }
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
        return result;
    }

}