package com.bingo.framework.rpc.proxy.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.proxy.AbstractProxyFactory;
import com.bingo.framework.rpc.proxy.AbstractProxyInvoker;
import com.bingo.framework.rpc.proxy.InvokerInvocationHandler;

/**
 * JavaassistRpcProxyFactory

 * @author william.liangf
 */
public class JdkProxyFactory extends AbstractProxyFactory {

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
    }

    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName, 
                                      Class<?>[] parameterTypes, 
                                      Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }

}