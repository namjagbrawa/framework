package com.bingo.framework.rpc.protocol.bingo.status;

import java.util.Collection;

import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.common.status.Status;
import com.bingo.framework.common.status.StatusChecker;
import com.bingo.framework.remoting.exchange.ExchangeServer;
import com.bingo.framework.rpc.protocol.bingo.BingoProtocol;

/**
 * ServerStatusChecker
 * 
 * @author william.liangf
 */
@Activate
public class ServerStatusChecker implements StatusChecker {

    public Status check() {
        Collection<ExchangeServer> servers = BingoProtocol.getBingoProtocol().getServers();
        if (servers == null || servers.size() == 0) {
            return new Status(Status.Level.UNKNOWN);
        }
        Status.Level level = Status.Level.OK;
        StringBuilder buf = new StringBuilder();
        for (ExchangeServer server : servers) {
            if (! server.isBound()) {
                level = Status.Level.ERROR;
                buf.setLength(0);
                buf.append(server.getLocalAddress());
                break;
            }
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append(server.getLocalAddress());
            buf.append("(clients:");
            buf.append(server.getChannels().size());
            buf.append(")");
        }
        return new Status(level, buf.toString());
    }

}