
package com.bingo.framework.rpc.cluster.directory;

import java.util.List;

import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Router;

/**
 * StaticDirectory
 * 
 * @author william.liangf
 */
public class StaticDirectory<T> extends AbstractDirectory<T> {
    
    private final List<Invoker<T>> invokers;
    
    public StaticDirectory(List<Invoker<T>> invokers){
        this(null, invokers, null);
    }
    
    public StaticDirectory(List<Invoker<T>> invokers, List<Router> routers){
        this(null, invokers, routers);
    }
    
    public StaticDirectory(URL url, List<Invoker<T>> invokers) {
        this(url, invokers, null);
    }

    public StaticDirectory(URL url, List<Invoker<T>> invokers, List<Router> routers) {
        super(url == null && invokers != null && invokers.size() > 0 ? invokers.get(0).getUrl() : url, routers);
        if (invokers == null || invokers.size() == 0)
            throw new IllegalArgumentException("invokers == null");
        this.invokers = invokers;
    }

    public Class<T> getInterface() {
        return invokers.get(0).getInterface();
    }

    public boolean isAvailable() {
        if (isDestroyed()) {
            return false;
        }
        for (Invoker<T> invoker : invokers) {
            if (invoker.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public void destroy() {
        if(isDestroyed()) {
            return;
        }
        super.destroy();
        for (Invoker<T> invoker : invokers) {
            invoker.destroy();
        }
        invokers.clear();
    }
    
    @Override
    protected List<Invoker<T>> doList(Invocation invocation) throws RpcException {

        return invokers;
    }

}