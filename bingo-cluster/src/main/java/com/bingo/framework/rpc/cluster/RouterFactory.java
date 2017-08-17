
package com.bingo.framework.rpc.cluster;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;
import com.bingo.framework.rpc.Invocation;

/**
 * RouterFactory. (SPI, Singleton, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Routing">Routing</a>
 * 
 * @see Cluster#join(Directory)
 * @see Directory#list(Invocation)
 * @author chao.liuc
 */
@SPI
public interface RouterFactory {
    
    /**
     * Create router.
     * 
     * @param url
     * @return router
     */
    @Adaptive("protocol")
    Router getRouter(URL url);
    
}