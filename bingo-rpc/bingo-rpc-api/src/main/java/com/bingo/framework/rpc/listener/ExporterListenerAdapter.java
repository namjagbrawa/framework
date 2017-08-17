package com.bingo.framework.rpc.listener;

import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.ExporterListener;
import com.bingo.framework.rpc.RpcException;

/**
 * ExporterListenerAdapter
 * 
 * @author william.liangf
 */
public abstract class ExporterListenerAdapter implements ExporterListener {

    public void exported(Exporter<?> exporter) throws RpcException {
    }

    public void unexported(Exporter<?> exporter) throws RpcException {
    }

}