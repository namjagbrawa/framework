package com.bingo.framework.rpc.protocol.bingo.telnet;

import java.util.Collection;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.exchange.ExchangeChannel;
import com.bingo.framework.remoting.exchange.ExchangeServer;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.telnet.support.Help;
import com.bingo.framework.rpc.protocol.bingo.BingoProtocol;

/**
 * ServerTelnetHandler
 * 
 * @author william.liangf
 */
@Activate
@Help(parameter = "[-l] [port]", summary = "Print server ports and connections.", detail = "Print server ports and connections.")
public class PortTelnetHandler implements TelnetHandler {

    public String telnet(Channel channel, String message) {
        StringBuilder buf = new StringBuilder();
        String port = null;
        boolean detail = false;
        if (message.length() > 0) {
            String[] parts = message.split("\\s+");
            for (String part : parts) {
                if ("-l".equals(part)) {
                    detail = true;
                } else {
                    if (! StringUtils.isInteger(part)) {
                        return "Illegal port " + part + ", must be integer.";
                    }
                    port = part;
                }
            }
        }
        if (port == null || port.length() == 0) {
            for (ExchangeServer server : BingoProtocol.getBingoProtocol().getServers()) {
                if (buf.length() > 0) {
                    buf.append("\r\n");
                }
                if (detail) {
                    buf.append(server.getUrl().getProtocol() + "://" + server.getUrl().getAddress());
                } else {
                    buf.append(server.getUrl().getPort());
                }
            }
        } else {
            int p = Integer.parseInt(port);
            ExchangeServer server = null;
            for (ExchangeServer s : BingoProtocol.getBingoProtocol().getServers()) {
                if (p == s.getUrl().getPort()) {
                    server = s;
                    break;
                }
            }
            if (server != null) {
                Collection<ExchangeChannel> channels = server.getExchangeChannels();
                for (ExchangeChannel c : channels) {
                    if (buf.length() > 0) {
                        buf.append("\r\n");
                    }
                    if (detail) {
                        buf.append(c.getRemoteAddress() + " -> " + c.getLocalAddress());
                    } else {
                        buf.append(c.getRemoteAddress());
                    }
                }
            } else {
                buf.append("No such port " + port);
            }
        }
        return buf.toString();
    }

}