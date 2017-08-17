package com.bingo.framework.rpc.protocol.bingo;

import java.util.Map;

import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.protocol.AbstractExporter;

/**
 * BingoExporter
 * 
 * @author william.liangf
 */
public class BingoExporter<T> extends AbstractExporter<T> {

    private final String                        key;

    private final Map<String, Exporter<?>> exporterMap;

    public BingoExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap){
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    @Override
    public void unexport() {
        super.unexport();
        exporterMap.remove(key);
    }

}