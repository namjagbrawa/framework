package com.bingo.framework.rpc.proxy.wrapper;

import java.lang.reflect.Constructor;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.Version;
import com.bingo.framework.common.bytecode.Wrapper;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.ConfigUtils;
import com.bingo.framework.common.utils.NetUtils;
import com.bingo.framework.common.utils.ReflectUtils;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Protocol;
import com.bingo.framework.rpc.ProxyFactory;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.service.GenericService;

/**
 * StubProxyFactoryWrapper
 * 
 * @author william.liangf
 */
public class StubProxyFactoryWrapper implements ProxyFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StubProxyFactoryWrapper.class);
    
    private final ProxyFactory proxyFactory;
    
    private Protocol protocol;
    
    public StubProxyFactoryWrapper(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        T proxy = proxyFactory.getProxy(invoker);
        if (GenericService.class != invoker.getInterface()) {
            String stub = invoker.getUrl().getParameter(Constants.STUB_KEY, invoker.getUrl().getParameter(Constants.LOCAL_KEY));
            if (ConfigUtils.isNotEmpty(stub)) {
                Class<?> serviceType = invoker.getInterface();
                if (ConfigUtils.isDefault(stub)) {
                    if (invoker.getUrl().hasParameter(Constants.STUB_KEY)) {
                        stub = serviceType.getName() + "Stub";
                    } else {
                        stub = serviceType.getName() + "Local";
                    }
                }
                try {
                    Class<?> stubClass = ReflectUtils.forName(stub);
                    if (! serviceType.isAssignableFrom(stubClass)) {
                        throw new IllegalStateException("The stub implemention class " + stubClass.getName() + " not implement interface " + serviceType.getName());
                    }
                    try {
                        Constructor<?> constructor = ReflectUtils.findConstructor(stubClass, serviceType);
                        proxy = (T) constructor.newInstance(new Object[] {proxy});
                        //export stub service
                        URL url = invoker.getUrl();
                        if (url.getParameter(Constants.STUB_EVENT_KEY, Constants.DEFAULT_STUB_EVENT)){
                            url = url.addParameter(Constants.STUB_EVENT_METHODS_KEY, StringUtils.join(Wrapper.getWrapper(proxy.getClass()).getDeclaredMethodNames(), ","));
                            url = url.addParameter(Constants.IS_SERVER_KEY, Boolean.FALSE.toString());
                            try{
                                export(proxy, (Class)invoker.getInterface(), url);
                            }catch (Exception e) {
                                LOGGER.error("export a stub service error.", e);
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException("No such constructor \"public " + stubClass.getSimpleName() + "(" + serviceType.getName() + ")\" in stub implemention class " + stubClass.getName(), e);
                    }
                } catch (Throwable t) {
                    LOGGER.error("Failed to create stub implemention class " + stub + " in consumer " + NetUtils.getLocalHost() + " use bingo version " + Version.getVersion() + ", cause: " + t.getMessage(), t);
                    // ignore
                }
            }
        }
        return proxy;
    }
    
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return proxyFactory.getInvoker(proxy, type, url);
    }
    
    private <T> Exporter<T> export(T instance, Class<T> type, URL url) {
        return protocol.export(proxyFactory.getInvoker(instance, type, url));
    }
    
}