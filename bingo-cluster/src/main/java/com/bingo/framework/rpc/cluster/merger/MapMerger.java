
package com.bingo.framework.rpc.cluster.merger;

import java.util.HashMap;
import java.util.Map;

import com.bingo.framework.rpc.cluster.Merger;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
public class MapMerger implements Merger<Map<?, ?>> {

    public Map<?, ?> merge(Map<?, ?>... items) {
        if (items.length == 0) {
            return null;
        }
        Map<Object, Object> result = new HashMap<Object, Object>();
        for (Map<?, ?> item : items) {
            if (item != null) {
                result.putAll(item);
            }
        }
        return result;
    }

}
