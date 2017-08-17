
package com.bingo.framework.rpc.cluster.loadbalance;

import java.util.List;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.cluster.LoadBalance;

/**
 * AbstractLoadBalance
 * 
 * @author william.liangf
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        if (invokers == null || invokers.size() == 0)
            return null;
        if (invokers.size() == 1)
            return invokers.get(0);
        return doSelect(invokers, url, invocation);
    }

    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation);

    protected int getWeight(Invoker<?> invoker, Invocation invocation) {
        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
        if (weight > 0) {
	        long timestamp = invoker.getUrl().getParameter(Constants.TIMESTAMP_KEY, 0L);
	    	if (timestamp > 0L) {
	    		int uptime = (int) (System.currentTimeMillis() - timestamp);
	    		int warmup = invoker.getUrl().getParameter(Constants.WARMUP_KEY, Constants.DEFAULT_WARMUP);
	    		if (uptime > 0 && uptime < warmup) {
	    			weight = calculateWarmupWeight(uptime, warmup, weight);
	    		}
	    	}
        }
    	return weight;
    }
    
    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
    	int ww = (int) ( (float) uptime / ( (float) warmup / (float) weight ) );
    	return ww < 1 ? 1 : (ww > weight ? weight : ww);
    }

}