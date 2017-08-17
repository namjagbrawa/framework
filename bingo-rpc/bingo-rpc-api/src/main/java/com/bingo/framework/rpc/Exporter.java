package com.bingo.framework.rpc;

import com.bingo.framework.rpc.protocol.AbstractExporter;

/**
 * Exporter. (API/SPI, Prototype, ThreadSafe)
 * 
 * @see Protocol#export(Invoker)
 * @see ExporterListener
 * @see AbstractExporter
 * @author william.liangf
 */
public interface Exporter<T> {
    
    /**
     * get invoker.
     * 
     * @return invoker
     */
    Invoker<T> getInvoker();
    
    /**
     * unexport.
     * 
     * <code>
     *     getInvoker().destroy();
     * </code>
     */
    void unexport();

}