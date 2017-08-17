package com.bingo.framework.rpc.protocol.bingo.telnet;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.protocol.bingo.BingoProtocol;

/**
 * ChangeServiceTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[service]", summary = "Change default service.", detail = "Change default service.")
public class ChangeTelnetHandler implements TelnetHandler {
    
    public static final String SERVICE_KEY = "telnet.service";

    public String telnet(Channel channel, String message) {
        if (message == null || message.length() == 0) {
            return "Please input service name, eg: \r\ncd XxxService\r\ncd com.xxx.XxxService";
        }
        StringBuilder buf = new StringBuilder();
        if (message.equals("/") || message.equals("..")) {
            String service = (String) channel.getAttribute(SERVICE_KEY);
            channel.removeAttribute(SERVICE_KEY);
            buf.append("Cancelled default service " + service + ".");
        } else {
            boolean found = false;
            for (Exporter<?> exporter : BingoProtocol.getBingoProtocol().getExporters()) {
                if (message.equals(exporter.getInvoker().getInterface().getSimpleName())
                        || message.equals(exporter.getInvoker().getInterface().getName())
                        || message.equals(exporter.getInvoker().getUrl().getPath())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                channel.setAttribute(SERVICE_KEY, message);
                buf.append("Used the " + message + " as default.\r\nYou can cancel default service by command: cd /");
            } else {
                buf.append("No such service " + message);
            }
        }
        return buf.toString();
    }

}