package com.bingo.framework.remoting.exchange.support;

import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ExchangeChannel;
import com.bingo.framework.remoting.exchange.ExchangeHandler;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.TelnetHandlerAdapter;
import com.bingo.framework.remoting.transport.ChannelHandlerDispatcher;

/**
 * ExchangeHandlerDispatcher
 * 
 * @author william.liangf
 */
public class ExchangeHandlerDispatcher implements ExchangeHandler {

    private final ReplierDispatcher replierDispatcher;

    private final ChannelHandlerDispatcher handlerDispatcher;

    private final TelnetHandler telnetHandler;
    
    public ExchangeHandlerDispatcher() {
        replierDispatcher = new ReplierDispatcher();
        handlerDispatcher = new ChannelHandlerDispatcher();
        telnetHandler = new TelnetHandlerAdapter();
    }
    
    public ExchangeHandlerDispatcher(Replier<?> replier){
        replierDispatcher = new ReplierDispatcher(replier);
        handlerDispatcher = new ChannelHandlerDispatcher();
        telnetHandler = new TelnetHandlerAdapter();
    }
    
    public ExchangeHandlerDispatcher(ChannelHandler... handlers){
        replierDispatcher = new ReplierDispatcher();
        handlerDispatcher = new ChannelHandlerDispatcher(handlers);
        telnetHandler = new TelnetHandlerAdapter();
    }
    
    public ExchangeHandlerDispatcher(Replier<?> replier, ChannelHandler... handlers){
        replierDispatcher = new ReplierDispatcher(replier);
        handlerDispatcher = new ChannelHandlerDispatcher(handlers);
        telnetHandler = new TelnetHandlerAdapter();
    }

    public ExchangeHandlerDispatcher addChannelHandler(ChannelHandler handler) {
        handlerDispatcher.addChannelHandler(handler);
        return this;
    }

    public ExchangeHandlerDispatcher removeChannelHandler(ChannelHandler handler) {
        handlerDispatcher.removeChannelHandler(handler);
        return this;
    }

    public <T> ExchangeHandlerDispatcher addReplier(Class<T> type, Replier<T> replier) {
        replierDispatcher.addReplier(type, replier);
        return this;
    }

    public <T> ExchangeHandlerDispatcher removeReplier(Class<T> type) {
        replierDispatcher.removeReplier(type);
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object reply(ExchangeChannel channel, Object request) throws RemotingException {
        return ((Replier)replierDispatcher).reply(channel, request);
    }

    public void connected(Channel channel) {
        handlerDispatcher.connected(channel);
    }

    public void disconnected(Channel channel) {
        handlerDispatcher.disconnected(channel);
    }

    public void sent(Channel channel, Object message) {
        handlerDispatcher.sent(channel, message);
    }

    public void received(Channel channel, Object message) {
        handlerDispatcher.received(channel, message);
    }

    public void caught(Channel channel, Throwable exception) {
        handlerDispatcher.caught(channel, exception);
    }

    public String telnet(Channel channel, String message) throws RemotingException {
        return telnetHandler.telnet(channel, message);
    }

}