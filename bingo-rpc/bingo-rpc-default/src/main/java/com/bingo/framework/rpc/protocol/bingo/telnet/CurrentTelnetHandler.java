package com.bingo.framework.rpc.protocol.bingo.telnet;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;

/**
 * CurrentServiceTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "", summary = "Print working default service.", detail = "Print working default service.")
public class CurrentTelnetHandler implements TelnetHandler {
    
    public String telnet(Channel channel, String message) {
        if (message.length() > 0) {
            return "Unsupported parameter " + message + " for pwd.";
        }
        String service = (String) channel.getAttribute(ChangeTelnetHandler.SERVICE_KEY);
        StringBuilder buf = new StringBuilder();
        if (service == null || service.length() == 0) {
            buf.append("/");
        } else {
            buf.append(service);
        }
        return buf.toString();
    }

}