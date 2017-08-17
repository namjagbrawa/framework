
package com.bingo.framework.common.status;

import com.bingo.framework.common.extension.SPI;

/**
 * StatusChecker
 * 
 * @author william.liangf
 */
@SPI
public interface StatusChecker {
    
    /**
     * check status
     * 
     * @return status
     */
    Status check();

}