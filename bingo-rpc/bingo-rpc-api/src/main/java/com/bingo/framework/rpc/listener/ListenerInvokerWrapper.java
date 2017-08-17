package com.bingo.framework.rpc.listener;

import java.util.List;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.InvokerListener;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Result;

/**
 * ListenerInvoker
 * 
 * @author william.liangf
 */
public class ListenerInvokerWrapper<T> implements Invoker<T> {

    private static final Logger logger = LoggerFactory.getLogger(ListenerInvokerWrapper.class);

    private final Invoker<T> invoker;
    
    private final List<InvokerListener> listeners;

    public ListenerInvokerWrapper(Invoker<T> invoker, List<InvokerListener> listeners){
        if (invoker == null) {
            throw new IllegalArgumentException("invoker == null");
        }
        this.invoker = invoker;
        this.listeners = listeners;
        if (listeners != null && listeners.size() > 0) {
            for (InvokerListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.referred(invoker);
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                }
            }
        }
    }

    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    public URL getUrl() {
        return invoker.getUrl();
    }

    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    public Result invoke(Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }
    
    @Override
    public String toString() {
        return getInterface() + " -> " + getUrl()==null?" ":getUrl().toString();
    }

    public void destroy() {
        try {
            invoker.destroy();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                for (InvokerListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.destroyed(invoker);
                        } catch (Throwable t) {
                            logger.error(t.getMessage(), t);
                        }
                    }
                }
            }
        }
    }

}