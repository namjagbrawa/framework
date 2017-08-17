
package com.bingo.framework.rpc.cluster;

import com.bingo.framework.common.extension.SPI;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
@SPI
public interface Merger<T> {

    T merge(T... items);

}
