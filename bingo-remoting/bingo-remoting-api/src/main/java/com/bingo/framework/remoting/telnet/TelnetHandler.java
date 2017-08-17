package com.bingo.framework.remoting.telnet;

import com.bingo.framework.common.extension.SPI;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.RemotingException;

/**
 * TelnetHandler
 * 
 * @author william.liangf
 */
@SPI
public interface TelnetHandler {

    /**
     * telnet.
     * 
     * @param channel
     * @param message
     */
    String telnet(Channel channel, String message) throws RemotingException;

}