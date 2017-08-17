
package com.bingo.framework.common;

/**
 * Node. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author william.liangf
 */
public interface Node {

    /**
     * get url.
     * 
     * @return url.
     */
    URL getUrl();
    
    /**
     * is available.
     * 
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();

}