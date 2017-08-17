package com.bingo.framework.remoting.transport;

import com.bingo.framework.remoting.exchange.support.MultiMessage;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.RemotingException;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 * @see MultiMessage
 */
public class MultiMessageHandler extends AbstractChannelHandlerDelegate {

    public MultiMessageHandler(ChannelHandler handler) {
        super(handler);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof MultiMessage) {
            MultiMessage list = (MultiMessage)message;
            for(Object obj : list) {
                handler.received(channel, obj);
            }
        } else {
            handler.received(channel, message);
        }
    }
}
