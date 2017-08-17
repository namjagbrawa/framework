package com.bingo.framework.demo.client.network;

import com.bingo.framework.demo.client.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by ZhangGe on 2017/4/4.
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

    private int maxContentLength = 0;


    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(2048, 4, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byteBuf.markReaderIndex();

        ByteBuf frame = (ByteBuf) super.decode(ctx, byteBuf);

        if (null == frame) {
            logger.error("frame == null");
            return null;
        }

        ByteBuffer byteBuffer = frame.nioBuffer();
        int limit = byteBuffer.limit();
        short magic = byteBuffer.getShort();
        int code = byteBuffer.getShort();
        int length = byteBuffer.getInt();
        byte[] data = new byte[length];
        byteBuffer.get(data);

        System.out.println(limit);
        System.out.println(magic);
        System.out.println(code);
        System.out.println(length);
        System.out.println(data);

        return new ClientMessage(code, data);
    }
}
