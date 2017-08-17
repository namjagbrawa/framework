package com.bingo.framework.remoting.transport.netty;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.Codec2;
import com.bingo.framework.remoting.buffer.ChannelBuffer;
import com.bingo.framework.remoting.buffer.DynamicChannelBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.List;

/**
 * NettyCodecAdapter.
 * 
 * @author qian.lei
 */
final class NettyCodecAdapter {

    private final ChannelHandler encoder = new InternalEncoder();
    
    private final ChannelHandler decoder = new InternalDecoder();

    private final Codec2         codec;
    
    private final URL            url;
    
    private final int            bufferSize;
    
    private final com.bingo.framework.remoting.ChannelHandler handler;

    public NettyCodecAdapter(Codec2 codec, URL url, com.bingo.framework.remoting.ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
        int b = url.getPositiveParameter(Constants.BUFFER_KEY, Constants.DEFAULT_BUFFER_SIZE);
        this.bufferSize = b >= Constants.MIN_BUFFER_SIZE && b <= Constants.MAX_BUFFER_SIZE ? b : Constants.DEFAULT_BUFFER_SIZE;
    }

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    @ChannelHandler.Sharable
    private class InternalEncoder extends MessageToMessageEncoder<Object> {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            com.bingo.framework.remoting.buffer.ChannelBuffer buffer =
                com.bingo.framework.remoting.buffer.ChannelBuffers.dynamicBuffer(1024);
            NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
            try {
            	codec.encode(channel, buffer, msg);
            } finally {
                NettyChannel.removeChannelIfDisconnected(ctx.channel());
            }

            out.add(Unpooled.wrappedBuffer(buffer.toByteBuffer()));
        }
    }

    private class InternalDecoder extends SimpleChannelInboundHandler<ByteBuf> {

        private com.bingo.framework.remoting.buffer.ChannelBuffer buffer =
            com.bingo.framework.remoting.buffer.ChannelBuffers.EMPTY_BUFFER;

        @Override
        public void channelRead0(ChannelHandlerContext ctx, ByteBuf input) throws Exception {

            int readable = input.readableBytes();
            if (readable <= 0) {
                return;
            }

            com.bingo.framework.remoting.buffer.ChannelBuffer message;
            if (buffer.readable()) {
                if (buffer instanceof DynamicChannelBuffer) {
                    writeBytes(buffer, input);
                    message = buffer;
                } else {
                    int size = buffer.readableBytes() + input.readableBytes();
                    message = com.bingo.framework.remoting.buffer.ChannelBuffers.dynamicBuffer(
                        size > bufferSize ? size : bufferSize);
                    message.writeBytes(buffer, buffer.readableBytes());
                    writeBytes(message, input);
                }
            } else {
                message = com.bingo.framework.remoting.buffer.ChannelBuffers.buffer(input.capacity());
                writeBytes(message, input);
            }

            NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
            Object msg;
            int saveReaderIndex;

            try {
                // decode object.
                do {
                    saveReaderIndex = message.readerIndex();
                    try {
                        msg = codec.decode(channel, message);
                    } catch (IOException e) {
                        buffer = com.bingo.framework.remoting.buffer.ChannelBuffers.EMPTY_BUFFER;
                        throw e;
                    }
                    if (msg == Codec2.DecodeResult.NEED_MORE_INPUT) {
                        message.readerIndex(saveReaderIndex);
                        break;
                    } else {
                        if (saveReaderIndex == message.readerIndex()) {
                            buffer = com.bingo.framework.remoting.buffer.ChannelBuffers.EMPTY_BUFFER;
                            throw new IOException("Decode without read data.");
                        }
                        if (msg != null) {
                            ctx.fireChannelRead(msg);
                        }
                    }
                } while (message.readable());
            } finally {
                if (message.readable()) {
                    message.discardReadBytes();
                    buffer = message;
                } else {
                    buffer = com.bingo.framework.remoting.buffer.ChannelBuffers.EMPTY_BUFFER;
                }
                NettyChannel.removeChannelIfDisconnected(ctx.channel());
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.fireExceptionCaught(cause);
        }

        private void writeBytes(ChannelBuffer buffer, ByteBuf message){
            if(null != buffer && message != null && message.isReadable()){
                byte []bytes = new byte[message.readableBytes()];
                message.readBytes(bytes);
                buffer.writeBytes(bytes);
                bytes = null;
            }
        }
    }
}