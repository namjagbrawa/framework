
package com.bingo.framework.common.status.support;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.status.Status;
import com.bingo.framework.common.status.StatusChecker;

/**
 * Load Status
 * 
 * @author william.liangf
 */
@Activate
public class LoadStatusChecker implements StatusChecker {

    public Status check() {
    	OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    	double load;
    	try {
    	    Method method = OperatingSystemMXBean.class.getMethod("getSystemLoadAverage", new Class<?>[0]);
    	    load = (Double)method.invoke(operatingSystemMXBean, new Object[0]);
    	} catch (Throwable e) {
    	    load = -1;
    	}
    	int cpu = operatingSystemMXBean.getAvailableProcessors();
        return new Status(load < 0 ? Status.Level.UNKNOWN : (load < cpu ? Status.Level.OK : Status.Level.WARN), (load < 0 ? "" : "load:" + load + ",") + "cpu:" + cpu);
    }

}