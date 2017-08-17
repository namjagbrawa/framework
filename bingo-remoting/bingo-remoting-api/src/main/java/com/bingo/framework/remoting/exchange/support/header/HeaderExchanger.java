package com.bingo.framework.remoting.exchange.support.header;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.Transporters;
import com.bingo.framework.remoting.exchange.ExchangeClient;
import com.bingo.framework.remoting.exchange.ExchangeHandler;
import com.bingo.framework.remoting.exchange.ExchangeServer;
import com.bingo.framework.remoting.exchange.Exchanger;
import com.bingo.framework.remoting.transport.DecodeHandler;

/**
 * DefaultMessenger
 * 
 * @author william.liangf
 */
public class HeaderExchanger implements Exchanger {
    
    public static final String NAME = "header";

    public ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException {
        return new HeaderExchangeClient(Transporters.connect(url, new DecodeHandler(new HeaderExchangeHandler(handler))));
    }

    public ExchangeServer bind(URL url, ExchangeHandler handler) throws RemotingException {
        return new HeaderExchangeServer(Transporters.bind(url, new DecodeHandler(new HeaderExchangeHandler(handler))));
    }

}