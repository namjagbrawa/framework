package com.bingo.framework.remoting.telnet.support;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.ExtensionLoader;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.RemotingException;
import com.bingo.framework.remoting.telnet.TelnetHandler;
import com.bingo.framework.remoting.transport.ChannelHandlerAdapter;

/**
 * @author william.liangf
 */
public class TelnetHandlerAdapter extends ChannelHandlerAdapter implements TelnetHandler {

    private final ExtensionLoader<TelnetHandler> extensionLoader = ExtensionLoader.getExtensionLoader(TelnetHandler.class);

    public String telnet(Channel channel, String message) throws RemotingException {
        String prompt = channel.getUrl().getParameterAndDecoded(Constants.PROMPT_KEY, Constants.DEFAULT_PROMPT);
        boolean noprompt = message.contains("--no-prompt");
        message = message.replace("--no-prompt", "");
        StringBuilder buf = new StringBuilder();
        message = message.trim();
        String command;
        if (message.length() > 0) {
            int i = message.indexOf(' ');
            if (i > 0) {
                command = message.substring(0, i).trim();
                message = message.substring(i + 1).trim();
            } else {
                command = message;
                message = "";
            }
        } else {
            command = "";
        }
        if (command.length() > 0) {
            if (extensionLoader.hasExtension(command)) {
                try {
                    String result = extensionLoader.getExtension(command).telnet(channel, message);
                    if (result == null) {
                        return null;
                    }
                    buf.append(result);
                } catch (Throwable t) {
                    buf.append(t.getMessage());
                }
            } else {
                buf.append("Unsupported command: ");
                buf.append(command);
            }
        }
        if (buf.length() > 0) {
            buf.append("\r\n");
        }
        if (prompt != null && prompt.length() > 0 && ! noprompt) {
            buf.append(prompt);
        }
        return buf.toString();
    }

}