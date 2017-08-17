package com.bingo.framework.demo.client.network;

import com.bingo.framework.demo.client.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by ZhangGe on 2017/4/4.
 */
public class MessageEncoder extends MessageToByteEncoder<ClientMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ClientMessage clientMessage, ByteBuf byteBuf) throws Exception {
        byte[] bytes = clientMessage.getBytes();
        int code = clientMessage.getCode();
        byteBuf.writeShort(ClientMessage.magic);
        byteBuf.writeShort(code);
        if (clientMessage.getCode() == 1) {
            byteBuf.writeInt(0);
        }else {
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
