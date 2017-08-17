package com.bingo.framework.remoting;

import javax.sound.midi.Receiver;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;

/**
 * Transporter. (SPI, Singleton, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Transport_Layer">Transport Layer</a>
 * <a href="http://en.wikipedia.org/wiki/Client%E2%80%93server_model">Client/Server</a>
 * 
 * @see Transporters
 * @author ding.lid
 * @author william.liangf
 */
@SPI("netty")
public interface Transporter {

    /**
     * Bind a server.
     * 
     * @see Transporters#bind(URL, Receiver, ChannelHandler)
     * @param url server url
     * @param handler
     * @return server
     * @throws RemotingException 
     */
    @Adaptive({Constants.SERVER_KEY, Constants.TRANSPORTER_KEY})
    Server bind(URL url, ChannelHandler handler) throws RemotingException;

    /**
     * Connect to a server.
     * 
     * @see Transporters#connect(URL, Receiver, ChannelListener)
     * @param url server url
     * @param handler
     * @return client
     * @throws RemotingException 
     */
    @Adaptive({Constants.CLIENT_KEY, Constants.TRANSPORTER_KEY})
    Client connect(URL url, ChannelHandler handler) throws RemotingException;

}