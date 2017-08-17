package com.bingo.framework.remoting.transport.netty;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.*;

/**
 * @author wuwen
 */
public class NettyTransporter implements Transporter {

    public static final String NAME = "netty";
    
    public Server bind(URL url, ChannelHandler listener) throws RemotingException {
        return new NettyServer(url, listener);
    }

    public Client connect(URL url, ChannelHandler listener) throws RemotingException {
        return new NettyClient(url, listener);
    }

}
