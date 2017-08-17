package com.bingo.framework.rpc;

import com.bingo.framework.common.extension.SPI;

/**
 * ExporterListener. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI
public interface ExporterListener {

    /**
     * The exporter exported.
     * 
     * @see Protocol#export(Invoker)
     * @param exporter
     * @throws RpcException
     */
    void exported(Exporter<?> exporter) throws RpcException;

    /**
     * The exporter unexported.
     * 
     * @see Exporter#unexport()
     * @param exporter
     * @throws RpcException
     */
    void unexported(Exporter<?> exporter);

}