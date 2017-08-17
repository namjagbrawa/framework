package com.bingo.framework.remoting.transport;

import java.net.InetSocketAddress;

import com.bingo.framework.common.Parameters;
import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.ChannelHandler;
import com.bingo.framework.remoting.Client;
import com.bingo.framework.remoting.RemotingException;

/**
 * ClientDelegate
 * 
 * @author william.liangf
 */
public class ClientDelegate implements Client {
    
    private transient Client client;

    public ClientDelegate() {
    }

    public ClientDelegate(Client client){
        setClient(client);
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("client == null");
        }
        this.client = client;
    }

    public void reset(URL url) {
        client.reset(url);
    }
    
    @Deprecated
    public void reset(Parameters parameters){
        reset(getUrl().addParameters(parameters.getParameters()));
    }

    public URL getUrl() {
        return client.getUrl();
    }

    public InetSocketAddress getRemoteAddress() {
        return client.getRemoteAddress();
    }

    public void reconnect() throws RemotingException {
        client.reconnect();
    }

    public ChannelHandler getChannelHandler() {
        return client.getChannelHandler();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public InetSocketAddress getLocalAddress() {
        return client.getLocalAddress();
    }

    public boolean hasAttribute(String key) {
        return client.hasAttribute(key);
    }

    public void send(Object message) throws RemotingException {
        client.send(message);
    }

    public Object getAttribute(String key) {
        return client.getAttribute(key);
    }

    public void setAttribute(String key, Object value) {
        client.setAttribute(key, value);
    }

    public void send(Object message, boolean sent) throws RemotingException {
        client.send(message, sent);
    }

    public void removeAttribute(String key) {
        client.removeAttribute(key);
    }

    public void close() {
        client.close();
    }
    public void close(int timeout) {
        client.close(timeout);
    }

    public boolean isClosed() {
        return client.isClosed();
    }

}