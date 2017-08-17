package com.bingo.framework.rpc.cluster.loadbalance;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;

import java.util.List;

/**
 * Created by ZhangGe on 2017/7/11.
 */
public class DirectLoadBalance extends AbstractLoadBalance {

    private static final Logger logger = LoggerFactory.getLogger(DirectLoadBalance.class);

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        if (invocation == null || invocation.getArguments() == null || invocation.getArguments().length <= 0 || invocation.getArguments()[0] == null) {
            logger.error("DIRECT LOADBALANCE SELECT ERROR, INVOCATION OR PARAMETER IS NULL. INVOCATION : " + invocation);
            throw new RpcException("DIRECT LOADBALANCE SELECT ERROR, INVOCATION OR PARAMETER IS NULL. INVOCATION : " + invocation);
        }

        if (invocation.getArguments()[0] instanceof URL) {
            URL directUrl = (URL) invocation.getArguments()[0];
            if (directUrl == null) {
                logger.error("DIRECT LOADBALANCE SELECT URL IS NULL, INVOCATION : " + invocation);
                throw new RpcException("DIRECT LOADBALANCE SELECT URL IS NULL, INVOCATION : " + invocation);
            } else {
                for (Invoker<T> invoker : invokers) {
                    if (directUrl.getAddress().equals(invoker.getUrl().getAddress())) {  // bingo://host:port/service
                        return invoker;
                    }
                }

                logger.error("DIRECT LOADBALANCE NOT HAVE MATCH URL, URL : " + directUrl + " INVOKERS : " + invokers);
                throw new RpcException("DIRECT LOADBALANCE NOT HAVE MATCH URL, URL : " + directUrl + " INVOKERS : " + invokers);
            }
        }

        logger.warn("DIRECT LOADBALANCE DONT HAVE DIRECT URL, INVOCATION : " + invocation);
        throw new RpcException("DIRECT LOADBALANCE DONT HAVE DIRECT URL, INVOCATION : " + invocation);
    }
}
