package com.bingo.framework.rpc.proxy.javassist;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.bytecode.Proxy;
import com.bingo.framework.common.bytecode.Wrapper;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.proxy.AbstractProxyFactory;
import com.bingo.framework.rpc.proxy.AbstractProxyInvoker;
import com.bingo.framework.rpc.proxy.InvokerInvocationHandler;

/**
 * JavaassistRpcProxyFactory 

 * @author william.liangf
 */
public class JavassistProxyFactory extends AbstractProxyFactory {

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // TODO Wrapper类不能正确处理带$的类名
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName, 
                                      Class<?>[] parameterTypes, 
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }

}