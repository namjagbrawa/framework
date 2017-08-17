package com.bingo.framework.remoting.exchange.support;

import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ExchangeChannel;
import com.bingo.framework.remoting.exchange.ExchangeHandler;
import com.bingo.framework.remoting.telnet.support.TelnetHandlerAdapter;

/**
 * ExchangeHandlerAdapter
 * 
 * @author william.liangf
 */
public abstract class ExchangeHandlerAdapter extends TelnetHandlerAdapter implements ExchangeHandler {

    public Object reply(ExchangeChannel channel, Object msg) throws RemotingException {
        return null;
    }

}