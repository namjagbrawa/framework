
package com.bingo.framework.common.status.support;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.status.Status;
import com.bingo.framework.common.status.StatusChecker;

/**
 * MemoryStatus
 * 
 * @author william.liangf
 */
@Activate
public class MemoryStatusChecker implements StatusChecker {

    public Status check() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        boolean ok = (maxMemory - (totalMemory - freeMemory) > 2048); // 剩余空间小于2M报警
        String msg = "max:" + (maxMemory / 1024 / 1024) + "M,total:" 
        + (totalMemory / 1024 / 1024) + "M,used:" + ((totalMemory / 1024 / 1024) - (freeMemory / 1024 / 1024)) + "M,free:" + (freeMemory / 1024 / 1024) + "M";
        return new Status(ok ? Status.Level.OK : Status.Level.WARN, msg);
    }

}