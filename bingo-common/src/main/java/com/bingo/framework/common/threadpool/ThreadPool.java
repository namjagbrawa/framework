
package com.bingo.framework.common.threadpool;

import java.util.concurrent.Executor;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;

/**
 * ThreadPool
 * 
 * @author william.liangf
 */
@SPI("fixed")
public interface ThreadPool {
    
    /**
     * 线程池
     * 
     * @param url 线程参数
     * @return 线程池
     */
    @Adaptive({Constants.THREADPOOL_KEY})
    Executor getExecutor(URL url);

}