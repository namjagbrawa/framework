package com.bingo.framework.rpc.protocol.bingo.telnet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;
import com.bingo.framework.remoting.telnet.support.TelnetUtils;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.RpcStatus;
import com.bingo.framework.rpc.protocol.bingo.BingoProtocol;

/**
 * CountTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[service] [method] [times]", summary = "Count the service.", detail = "Count the service.")
public class CountTelnetHandler implements TelnetHandler {

    public String telnet(final Channel channel, String message) {
        String service = (String) channel.getAttribute(ChangeTelnetHandler.SERVICE_KEY);
        if ((service == null || service.length() == 0)
                && (message == null || message.length() == 0)) {
            return "Please input service name, eg: \r\ncount XxxService\r\ncount XxxService xxxMethod\r\ncount XxxService xxxMethod 10\r\nor \"cd XxxService\" firstly.";
        }
        StringBuilder buf = new StringBuilder();
        if (service != null && service.length() > 0) {
            buf.append("Use default service " + service + ".\r\n");
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
        final int t = Integer.parseInt(times);
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
            if (t > 0) {
                final String mtd = method;
                final Invoker<?> inv = invoker;
                final String prompt = channel.getUrl().getParameter("prompt", "telnet");
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        for (int i = 0; i < t; i ++) {
                            String result = count(inv, mtd);
                            try {
                                channel.send("\r\n" + result);
                            } catch (RemotingException e1) {
                                return;
                            }
                            if (i < t - 1) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                        try {
                            channel.send("\r\n" + prompt + "> ");
                        } catch (RemotingException e1) {
                            return;
                        }
                    }
                }, "TelnetCount");
                thread.setDaemon(true);
                thread.start();
            }
        } else {
            buf.append("No such service " + service);
        }
        return buf.toString();
    }
    
    private String count(Invoker<?> invoker, String method) {
        URL url = invoker.getUrl();
        List<List<String>> table = new ArrayList<List<String>>();
        List<String> header = new ArrayList<String>();
        header.add("method");
        header.add("total");
        header.add("failed");
        header.add("active");
        header.add("average");
        header.add("max");
        if (method == null || method.length() == 0) {
            for (Method m : invoker.getInterface().getMethods()) {
                RpcStatus count = RpcStatus.getStatus(url, m.getName());
                List<String> row = new ArrayList<String>();
                row.add(m.getName());
                row.add(String.valueOf(count.getTotal()));
                row.add(String.valueOf(count.getFailed()));
                row.add(String.valueOf(count.getActive()));
                row.add(String.valueOf(count.getSucceededAverageElapsed()) + "ms");
                row.add(String.valueOf(count.getSucceededMaxElapsed()) + "ms");
                table.add(row);
            }
        } else {
            boolean found = false;
            for (Method m : invoker.getInterface().getMethods()) {
                if (m.getName().equals(method)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                RpcStatus count = RpcStatus.getStatus(url, method);
                List<String> row = new ArrayList<String>();
                row.add(method);
                row.add(String.valueOf(count.getTotal()));
                row.add(String.valueOf(count.getFailed()));
                row.add(String.valueOf(count.getActive()));
                row.add(String.valueOf(count.getSucceededAverageElapsed()) + "ms");
                row.add(String.valueOf(count.getSucceededMaxElapsed()) + "ms");
                table.add(row);
            } else {
                return "No such method " + method + " in class " + invoker.getInterface().getName();
            }
        }
        return TelnetUtils.toTable(header, table);
    }

}