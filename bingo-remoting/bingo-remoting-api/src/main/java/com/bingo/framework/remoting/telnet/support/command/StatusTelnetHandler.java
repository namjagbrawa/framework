package com.bingo.framework.remoting.telnet.support.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.extension.ExtensionLoader;
import com.bingo.framework.common.status.Status;
import com.bingo.framework.common.status.StatusChecker;
import com.bingo.framework.common.status.support.StatusUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;
import com.bingo.framework.remoting.telnet.support.TelnetUtils;

/**
 * StatusTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[-l]", summary = "Show status.", detail = "Show status.")
public class StatusTelnetHandler implements TelnetHandler {

    private final ExtensionLoader<StatusChecker> extensionLoader = ExtensionLoader.getExtensionLoader(StatusChecker.class);

    public String telnet(Channel channel, String message) {
        if (message.equals("-l")) {
            List<StatusChecker> checkers = extensionLoader.getActivateExtension(channel.getUrl(), "status");
            String[] header = new String[] {"resource", "status", "message"};
            List<List<String>> table = new ArrayList<List<String>>();
            Map<String, Status> statuses = new HashMap<String, Status>();
            if (checkers != null && checkers.size() > 0) {
                for (StatusChecker checker : checkers) {
                    String name = extensionLoader.getExtensionName(checker);
                    Status stat;
                    try {
                        stat = checker.check();
                    } catch (Throwable t) {
                        stat = new Status(Status.Level.ERROR, t.getMessage());
                    }
                    statuses.put(name, stat);
                    if (stat.getLevel() != null && stat.getLevel() != Status.Level.UNKNOWN) {
                        List<String> row = new ArrayList<String>();
                        row.add(name);
                        row.add(String.valueOf(stat.getLevel()));
                        row.add(stat.getMessage() == null ? "" : stat.getMessage());
                        table.add(row);
                    }
                }
            }
            Status stat= StatusUtils.getSummaryStatus(statuses);
            List<String> row = new ArrayList<String>();
            row.add("summary");
            row.add(String.valueOf(stat.getLevel()));
            row.add(stat.getMessage());
            table.add(row);
            return TelnetUtils.toTable(header, table);
        } else if (message.length() > 0) {
            return "Unsupported parameter " + message + " for status.";
        }
        String status = channel.getUrl().getParameter("status");
        Map<String, Status> statuses = new HashMap<String, Status>();
        if (status != null && status.length() > 0) {
            String[] ss = Constants.COMMA_SPLIT_PATTERN.split(status);
            for (String s : ss) {
                StatusChecker handler = extensionLoader.getExtension(s);
                Status stat;
                try {
                    stat = handler.check();
                } catch (Throwable t) {
                    stat = new Status(Status.Level.ERROR, t.getMessage());
                }
                statuses.put(s, stat);
            }
        }
        Status stat= StatusUtils.getSummaryStatus(statuses);
        return String.valueOf(stat.getLevel());
    }

}