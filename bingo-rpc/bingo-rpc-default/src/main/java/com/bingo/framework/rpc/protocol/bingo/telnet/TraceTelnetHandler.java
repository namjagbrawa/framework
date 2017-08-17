package com.bingo.framework.rpc.protocol.bingo.telnet;

import java.lang.reflect.Method;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.protocol.bingo.BingoProtocol;
import com.bingo.framework.rpc.protocol.bingo.filter.TraceFilter;

/**
 * TraceTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[service] [method] [times]", summary = "Trace the service.", detail = "Trace the service.")
public class TraceTelnetHandler implements TelnetHandler {

    public String telnet(Channel channel, String message) {
        String service = (String) channel.getAttribute(ChangeTelnetHandler.SERVICE_KEY);
        if ((service == null || service.length() == 0)
                && (message == null || message.length() == 0)) {
            return "Please input service name, eg: \r\ntrace XxxService\r\ntrace XxxService xxxMethod\r\ntrace XxxService xxxMethod 10\r\nor \"cd XxxService\" firstly.";
        }
        String[] parts = message.split("\\s+");
        String method;
        String times;
        if (service == null || service.length() == 0) {
            service = parts.length > 0 ? parts[0] : null;
            method = parts.length > 1 ? parts[1] : null;
        } else {
            method = parts.length > 0 ? parts[0] : null;
        }
        if (StringUtils.isInteger(method)) {
            times = method;
            method = null;
        } else {
            times = parts.length > 2 ? parts[2] : "1";
        }
        if (! StringUtils.isInteger(times)) {
            return "Illegal times " + times + ", must be integer.";
        }
        Invoker<?> invoker = null;
        for (Exporter<?> exporter : BingoProtocol.getBingoProtocol().getExporters()) {
            if (service.equals(exporter.getInvoker().getInterface().getSimpleName())
                    || service.equals(exporter.getInvoker().getInterface().getName())
                    || service.equals(exporter.getInvoker().getUrl().getPath())) {
                invoker = exporter.getInvoker();
                break;
            }
        }
        if (invoker != null) {
            if (method != null && method.length() > 0) {
                boolean found = false;
                for (Method m : invoker.getInterface().getMethods()) {
                    if (m.getName().equals(method)) {
                        found = true;
                        break;
                    }
                }
                if (! found) {
                    return "No such method " + method + " in class " + invoker.getInterface().getName();
                }
            }
            TraceFilter.addTracer(invoker.getInterface(), method, channel, Integer.parseInt(times));
        } else {
            return "No such service " + service;
        }
        return null;
    }

}