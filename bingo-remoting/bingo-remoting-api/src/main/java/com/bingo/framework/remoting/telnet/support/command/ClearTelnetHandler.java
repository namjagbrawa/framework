package com.bingo.framework.remoting.telnet.support.command;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;

/**
 * ClearTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[lines]", summary = "Clear screen.", detail = "Clear screen.")
public class ClearTelnetHandler implements TelnetHandler {

    public String telnet(Channel channel, String message) {
        int lines = 100;
        if (message.length() > 0) {
            if (! StringUtils.isInteger(message)) {
                return "Illegal lines " + message + ", must be integer.";
            }
            lines = Integer.parseInt(message);
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < lines; i ++) {
            buf.append("\r\n");
        }
        return buf.toString();
    }

}