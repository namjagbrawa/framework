package com.bingo.framework.registry.status;

import java.util.Collection;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.status.Status;
import com.bingo.framework.common.status.StatusChecker;
import com.bingo.framework.registry.Registry;
import com.bingo.framework.registry.support.AbstractRegistryFactory;

/**
 * RegistryStatusChecker
 * 
 * @author william.liangf
 */
@Activate
public class RegistryStatusChecker implements StatusChecker {

    public Status check() {
        Collection<Registry> regsitries = AbstractRegistryFactory.getRegistries();
        if (regsitries == null || regsitries.size() == 0) {
            return new Status(Status.Level.UNKNOWN);
        }
        Status.Level level = Status.Level.OK;
        StringBuilder buf = new StringBuilder();
        for (Registry registry : regsitries) {
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append(registry.getUrl().getAddress());
            if (! registry.isAvailable()) {
                level = Status.Level.ERROR;
                buf.append("(disconnected)");
            } else {
                buf.append("(connected)");
            }
        }
        return new Status(level, buf.toString());
    }

}