package com.bingo.framework.rpc.protocol.bingo.filter;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.ConcurrentHashSet;
import com.bingo.framework.common.utils.JSONUtil;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.rpc.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TraceFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.PROVIDER)
public class TraceFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(TraceFilter.class);
    
    private static final String TRACE_MAX = "trace.max";
    
    private static final String TRACE_COUNT = "trace.count";
    
    private static final ConcurrentMap<String, Set<Channel>> tracers = new ConcurrentHashMap<String, Set<Channel>>();
    
    public static void addTracer(Class<?> type, String method, Channel channel, int max) {
        channel.setAttribute(TRACE_MAX, max);
        channel.setAttribute(TRACE_COUNT, new AtomicInteger());
        String key = method != null && method.length() > 0 ? type.getName() + "." + method : type.getName();
        Set<Channel> channels = tracers.get(key);
        if (channels == null) {
            tracers.putIfAbsent(key, new ConcurrentHashSet<Channel>());
            channels = tracers.get(key);
        }
        channels.add(channel);
    }
    
    public static void removeTracer(Class<?> type, String method, Channel channel) {
        channel.removeAttribute(TRACE_MAX);
        channel.removeAttribute(TRACE_COUNT);
        String key = method != null && method.length() > 0 ? type.getName() + "." + method : type.getName();
        Set<Channel> channels = tracers.get(key);
        if (channels != null) {
            channels.remove(channel);
        }
    }
    
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long start = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        long end = System.currentTimeMillis();
        if (tracers.size() > 0) {
            String key = invoker.getInterface().getName() + "." + invocation.getMethodName();
            Set<Channel> channels = tracers.get(key);
            if (channels == null || channels.size() == 0) {
                key = invoker.getInterface().getName();
                channels = tracers.get(key);
            }
            if (channels != null && channels.size() > 0) {
                for (Channel channel : new ArrayList<Channel>(channels)) {
                    if (channel.isConnected()) {
                        try {
                            int max = 1;
                            Integer m = (Integer) channel.getAttribute(TRACE_MAX);
                            if (m != null) {
                                max = (int) m;
                            }
                            int count = 0;
                            AtomicInteger c = (AtomicInteger) channel.getAttribute(TRACE_COUNT);
                            if (c == null) {
                                c = new AtomicInteger();
                                channel.setAttribute(TRACE_COUNT, c);
                            }
                            count = c.getAndIncrement();
                            if (count < max) {
                                String prompt = channel.getUrl().getParameter(Constants.PROMPT_KEY, Constants.DEFAULT_PROMPT);
                                channel.send("\r\n" + RpcContext.getContext().getRemoteAddress() + " -> "
                                         + invoker.getInterface().getName() 
                                         + "." + invocation.getMethodName() 
                                         + "(" + JSONUtil.toJson(invocation.getArguments()) + ")" + " -> " + JSONUtil.toJson(result.getValue())
                                         + "\r\nelapsed: "+(end - start) +" ms."
                                         + "\r\n\r\n" + prompt);
                            }
                            if(count >= max - 1) {
                                channels.remove(channel);
                            }
                        } catch (Throwable e) {
                            channels.remove(channel);
                            logger.warn(e.getMessage(), e);
                        }
                    } else {
                        channels.remove(channel);
                    }
                }
            }
        }
        return result;
    }

}