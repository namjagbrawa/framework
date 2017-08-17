package com.bingo.framework.remoting;

import com.bingo.framework.common.Resetable;
import com.bingo.framework.common.Parameters;
import com.bingo.framework.common.URL;

/**
 * Remoting Client. (API/SPI, Prototype, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Client%E2%80%93server_model">Client/Server</a>
 * 
 * @see Transporter#connect(URL, ChannelHandler)
 * @author qian.lei
 */
public interface Client extends Endpoint, Channel, Resetable {

    /**
     * reconnect.
     */
    void reconnect() throws RemotingException;
    
    @Deprecated
    void reset(Parameters parameters);
    
}