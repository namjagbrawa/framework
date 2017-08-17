package com.bingo.framework.rpc;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.SPI;

/**
 * InvokerListener. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI
public interface InvokerListener {

    /**
     * The invoker referred
     * 
     * @see Protocol#refer(Class, URL)
     * @param invoker
     * @throws RpcException
     */
    void referred(Invoker<?> invoker) throws RpcException;

    /**
     * The invoker destroyed.
     * 
     * @see Invoker#destroy()
     * @param invoker
     */
    void destroyed(Invoker<?> invoker);

}