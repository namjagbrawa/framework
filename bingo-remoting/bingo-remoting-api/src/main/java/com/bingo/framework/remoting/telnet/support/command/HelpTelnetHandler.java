package com.bingo.framework.remoting.telnet.support.command;

import java.util.ArrayList;
import java.util.List;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.extension.ExtensionLoader;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;
import com.bingo.framework.remoting.telnet.support.TelnetUtils;

/**
 * HelpTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[command]", summary = "Show help.", detail = "Show help.")
public class HelpTelnetHandler implements TelnetHandler {
    
    private final ExtensionLoader<TelnetHandler> extensionLoader = ExtensionLoader.getExtensionLoader(TelnetHandler.class);

    public String telnet(Channel channel, String message) {
        if (message.length() > 0) {
            if (! extensionLoader.hasExtension(message)) {
                return "No such command " + message;
            }
            TelnetHandler handler = extensionLoader.getExtension(message);
            Help help = handler.getClass().getAnnotation(Help.class);
            StringBuilder buf = new StringBuilder();
            buf.append("Command:\r\n    ");
            buf.append(message + " " + help.parameter().replace("\r\n", " ").replace("\n", " "));
            buf.append("\r\nSummary:\r\n    ");
            buf.append(help.summary().replace("\r\n", " ").replace("\n", " "));
            buf.append("\r\nDetail:\r\n    ");
            buf.append(help.detail().replace("\r\n", "    \r\n").replace("\n", "    \n"));
            return buf.toString();
        } else {
            List<List<String>> table = new ArrayList<List<String>>();
            List<TelnetHandler> handlers = extensionLoader.getActivateExtension(channel.getUrl(), "telnet");
            if (handlers != null && handlers.size() > 0) {
                for (TelnetHandler handler : handlers) {
                    Help help = handler.getClass().getAnnotation(Help.class);
                    List<String> row = new ArrayList<String>();
                    String parameter = " " + extensionLoader.getExtensionName(handler) + " " + (help != null ? help.parameter().replace("\r\n", " ").replace("\n", " ") : "");
                    row.add(parameter.length() > 50 ? parameter.substring(0, 50) + "..." : parameter);
                    String summary = help != null ? help.summary().replace("\r\n", " ").replace("\n", " ") : "";
                    row.add(summary.length() > 50 ? summary.substring(0, 50) + "..." : summary);
                    table.add(row);
                }
            }
            return "Please input \"help [command]\" show detail.\r\n" + TelnetUtils.toList(table);
        }
    }

}