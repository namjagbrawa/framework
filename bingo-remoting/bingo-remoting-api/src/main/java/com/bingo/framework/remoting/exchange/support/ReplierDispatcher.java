package com.bingo.framework.remoting.exchange.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.exchange.ExchangeChannel;

/**
 * ReplierDispatcher
 * 
 * @author william.liangf
 */
public class ReplierDispatcher implements Replier<Object> {

    private final Replier<?> defaultReplier;
    
    private final Map<Class<?>, Replier<?>> repliers = new ConcurrentHashMap<Class<?>, Replier<?>>();

    public ReplierDispatcher(){
        this(null, null);
    }
    
    public ReplierDispatcher(Replier<?> defaultReplier){
        this(defaultReplier, null);
    }

    public ReplierDispatcher(Replier<?> defaultReplier, Map<Class<?>, Replier<?>> repliers){
        this.defaultReplier = defaultReplier;
        if (repliers != null && repliers.size() > 0) {
            this.repliers.putAll(repliers);
        }
    }

    public <T> ReplierDispatcher addReplier(Class<T> type, Replier<T> replier) {
        repliers.put(type, replier);
        return this;
    }

    public <T> ReplierDispatcher removeReplier(Class<T> type) {
        repliers.remove(type);
        return this;
    }

    private Replier<?> getReplier(Class<?> type) {
        for(Map.Entry<Class<?>, Replier<?>> entry : repliers.entrySet()) {
            if(entry.getKey().isAssignableFrom(type)) {
                return entry.getValue();
            }
        }
        if (defaultReplier != null) {
            return defaultReplier;
        }
        throw new IllegalStateException("Replier not found, Unsupported message object: " + type);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object reply(ExchangeChannel channel, Object request) throws RemotingException {
        return ((Replier)getReplier(request.getClass())).reply(channel, request);
    }

}