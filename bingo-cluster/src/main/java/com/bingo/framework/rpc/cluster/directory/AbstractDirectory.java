
package com.bingo.framework.rpc.cluster.directory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.ExtensionLoader;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.cluster.Router;
import com.bingo.framework.rpc.cluster.RouterFactory;
import com.bingo.framework.rpc.cluster.router.MockInvokersSelector;

/**
 * 增加router的Directory
 * 
 * @author chao.liuc
 */
public abstract class AbstractDirectory<T> implements Directory<T> {

    // 日志输出
    private static final Logger logger = LoggerFactory.getLogger(AbstractDirectory.class);

    private final URL url ;
    
    private volatile boolean destroyed = false;

    private volatile URL consumerUrl ;
    
	private volatile List<Router> routers;
    
    public AbstractDirectory(URL url) {
        this(url, null);
    }
    
    public AbstractDirectory(URL url, List<Router> routers) {
    	this(url, url, routers);
    }
    
    public AbstractDirectory(URL url, URL consumerUrl, List<Router> routers) {
        if (url == null)
            throw new IllegalArgumentException("url == null");
        this.url = url;
        this.consumerUrl = consumerUrl;
        setRouters(routers);
    }
    
    public List<Invoker<T>> list(Invocation invocation) throws RpcException {
        if (destroyed){
            throw new RpcException("Directory already destroyed .url: "+ getUrl());
        }
        List<Invoker<T>> invokers = doList(invocation);
        List<Router> localRouters = this.routers; // local reference
        if (localRouters != null && localRouters.size() > 0) {
            for (Router router: localRouters){
                try {
                    if (router.getUrl() == null || router.getUrl().getParameter(Constants.RUNTIME_KEY, true)) {
                        invokers = router.route(invokers, getConsumerUrl(), invocation);
                    }
                } catch (Throwable t) {
                    logger.error("Failed to execute router: " + getUrl() + ", cause: " + t.getMessage(), t);
                }
            }
        }
        return invokers;
    }
    
    public URL getUrl() {
        return url;
    }
    
    public List<Router> getRouters(){
        return routers;
    }

	public URL getConsumerUrl() {
		return consumerUrl;
	}

	public void setConsumerUrl(URL consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

    protected void setRouters(List<Router> routers){
        // copy list
        routers = routers == null ? new  ArrayList<Router>() : new ArrayList<Router>(routers);
        // append url router
    	String routerkey = url.getParameter(Constants.ROUTER_KEY);
        if (routerkey != null && routerkey.length() > 0) {
            RouterFactory routerFactory = ExtensionLoader.getExtensionLoader(RouterFactory.class).getExtension(routerkey);
            routers.add(routerFactory.getRouter(url));
        }
        // append mock invoker selector
        routers.add(new MockInvokersSelector());
        Collections.sort(routers);
    	this.routers = routers;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy(){
        destroyed = true;
    }

    protected abstract List<Invoker<T>> doList(Invocation invocation) throws RpcException ;

}