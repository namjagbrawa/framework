package com.bingo.framework.remoting;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.exchange.ExchangeChannel;
import com.bingo.framework.remoting.exchange.ResponseFuture;

import java.net.InetSocketAddress;

/**
 * RemotingException. (API, Prototype, ThreadSafe)
 * 
 * @see ResponseFuture#get()
 * @see ResponseFuture#get(int)
 * @see Channel#send(Object, boolean)
 * @see ExchangeChannel#request(Object)
 * @see ExchangeChannel#request(Object, int)
 * @see Transporter#bind(URL, ChannelHandler)
 * @see Transporter#connect(URL, ChannelHandler)
 * @author qian.lei
 * @export
 */
public class RemotingException extends Exception {

    private static final long serialVersionUID = -3160452149606778709L;

    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;

    public RemotingException(Channel channel, String msg){
        this(channel == null ? null : channel.getLocalAddress(), channel == null ? null : channel.getRemoteAddress(),
             msg);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message){
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Channel channel, Throwable cause){
        this(channel == null ? null : channel.getLocalAddress(), channel == null ? null : channel.getRemoteAddress(),
             cause);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause){
        super(cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Channel channel, String message, Throwable cause){
        this(channel == null ? null : channel.getLocalAddress(), channel == null ? null : channel.getRemoteAddress(),
             message, cause);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message,
                             Throwable cause){
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}