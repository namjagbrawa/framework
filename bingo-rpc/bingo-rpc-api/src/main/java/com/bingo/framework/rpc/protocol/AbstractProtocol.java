package com.bingo.framework.rpc.protocol;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.ConcurrentHashSet;
import com.bingo.framework.common.utils.ConfigUtils;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Protocol;
import com.bingo.framework.rpc.support.ProtocolUtils;

/**
 * abstract ProtocolSupport.
 * 
 * @author qian.lei
 * @author william.liangf
 */
public abstract class AbstractProtocol implements Protocol {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();

	//TODO SOFEREFENCE
    protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();
    
	protected static String serviceKey(URL url) {
	    return ProtocolUtils.serviceKey(url);
	}

	protected static String serviceKey(int port, String serviceName, String serviceVersion, String serviceGroup) {
		return ProtocolUtils.serviceKey(port, serviceName, serviceVersion, serviceGroup);
	}
	
	public void destroy() {
	    for (Invoker<?> invoker : invokers){
	        if (invoker != null) {
	            invokers.remove(invoker);
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Destroy reference: " + invoker.getUrl());
                    }
                    invoker.destroy();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
	    }
	    for (String key : new ArrayList<String>(exporterMap.keySet())) {
            Exporter<?> exporter = exporterMap.remove(key);
            if (exporter != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Unexport service: " + exporter.getInvoker().getUrl());
                    }
                    exporter.unexport();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
	}
	@SuppressWarnings("deprecation")
    protected static int getServerShutdownTimeout() {
        int timeout = Constants.DEFAULT_SERVER_SHUTDOWN_TIMEOUT;
        String value = ConfigUtils.getProperty(Constants.SHUTDOWN_WAIT_KEY);
        if (value != null && value.length() > 0) {
            try{
                timeout = Integer.parseInt(value);
            }catch (Exception e) {
            }        
        } else {
            value = ConfigUtils.getProperty(Constants.SHUTDOWN_WAIT_SECONDS_KEY);
            if (value != null && value.length() > 0) {
                try{
                    timeout = Integer.parseInt(value) * 1000;
                }catch (Exception e) {
                }        
            }
        }
        
        return timeout;
    }
}