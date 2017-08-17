
package com.bingo.framework.common.status.support;

import java.util.Map;

import com.bingo.framework.common.status.Status;
import com.bingo.framework.common.status.Status.Level;

/**
 * StatusManager
 * 
 * @author william.liangf
 */
public class StatusUtils {
    
    public static Status getSummaryStatus(Map<String, Status> statuses) {
        Level level = Level.OK;
        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
            String key = entry.getKey();
            Status status = entry.getValue();
            Level l = status.getLevel();
            if (Level.ERROR.equals(l)) {
                level = Level.ERROR;
                if (msg.length() > 0) {
                    msg.append(",");
                }
                msg.append(key);
            } else if (Level.WARN.equals(l)) {
                if(! Level.ERROR.equals(level)) {
                    level = Level.WARN;
                }
                if (msg.length() > 0) {
                    msg.append(",");
                }
                msg.append(key);
            }
        }
        return new Status(level, msg.toString());
    }

}