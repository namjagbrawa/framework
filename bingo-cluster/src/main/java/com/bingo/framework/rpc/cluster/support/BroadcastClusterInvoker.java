/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bingo.framework.rpc.cluster.support;

import java.util.List;

import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcContext;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.cluster.LoadBalance;

/**
 * BroadcastClusterInvoker
 * 
 * @author william.liangf
 */
public class BroadcastClusterInvoker<T> extends AbstractClusterInvoker<T> {
    
    private static final Logger logger = LoggerFactory.getLogger(BroadcastClusterInvoker.class);

    public BroadcastClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Result doInvoke(final Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        checkInvokers(invokers, invocation);
        RpcContext.getContext().setInvokers((List)invokers);
        RpcException exception = null;
        Result result = null;
        for (Invoker<T> invoker: invokers) {
            try {
                result = invoker.invoke(invocation);
            } catch (RpcException e) {
                exception = e;
                logger.warn(e.getMessage(), e);
            } catch (Throwable e) {
                exception = new RpcException(e.getMessage(), e);
                logger.warn(e.getMessage(), e);
            }
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }

}