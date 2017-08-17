package com.bingo.framework.rpc.listener;

import java.util.List;

import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.ExporterListener;
import com.bingo.framework.rpc.Invoker;

/**
 * ListenerExporter
 * 
 * @author william.liangf
 */
public class ListenerExporterWrapper<T> implements Exporter<T> {

    private static final Logger logger = LoggerFactory.getLogger(ListenerExporterWrapper.class);

    private final Exporter<T> exporter;
    
    private final List<ExporterListener> listeners;

    public ListenerExporterWrapper(Exporter<T> exporter, List<ExporterListener> listeners){
        if (exporter == null) {
            throw new IllegalArgumentException("exporter == null");
        }
        this.exporter = exporter;
        this.listeners = listeners;
        if (listeners != null && listeners.size() > 0) {
            RuntimeException exception = null;
            for (ExporterListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.exported(this);
                    } catch (RuntimeException t) {
                        logger.error(t.getMessage(), t);
                        exception = t;
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
    }

    public Invoker<T> getInvoker() {
        return exporter.getInvoker();
    }

    public void unexport() {
        try {
            exporter.unexport();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                RuntimeException exception = null;
                for (ExporterListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.unexported(this);
                        } catch (RuntimeException t) {
                            logger.error(t.getMessage(), t);
                            exception = t;
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
            }
        }
    }

}