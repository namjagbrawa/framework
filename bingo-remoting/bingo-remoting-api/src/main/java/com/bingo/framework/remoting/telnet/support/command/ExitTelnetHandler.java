package com.bingo.framework.remoting.telnet.support.command;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;

/**
 * ExitTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "", summary = "Exit the telnet.", detail = "Exit the telnet.")
public class ExitTelnetHandler implements TelnetHandler {

    public String telnet(Channel channel, String message) {
        channel.close();
        return null;
    }

}