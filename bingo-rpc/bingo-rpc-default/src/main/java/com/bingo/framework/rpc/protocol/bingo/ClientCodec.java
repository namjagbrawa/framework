package com.bingo.framework.rpc.protocol.bingo;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.io.Bytes;
import com.bingo.framework.common.io.StreamUtils;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.IDGen;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.Codec2;
import com.bingo.framework.remoting.buffer.ChannelBuffer;
import com.bingo.framework.remoting.buffer.ChannelBufferInputStream;
import com.bingo.framework.remoting.exchange.Request;
import com.bingo.framework.remoting.exchange.Response;
import com.bingo.framework.remoting.telnet.codec.TelnetCodec;
import com.bingo.framework.rpc.RpcInvocation;
import com.bingo.framework.rpc.RpcResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Client codec.
 * 2byte magic, 2byte code, 4byte length.
 *
 * @author ZhangGe
 */
public class ClientCodec extends TelnetCodec implements Codec2 {

    private static final Logger logger = LoggerFactory.getLogger(ClientCodec.class);

    // header length.
    protected static final int HEADER_LENGTH = 8;

    // magic header.
    protected static final short MAGIC = (short) 0xACCA;

    protected static final byte MAGIC_HIGH = Bytes.short2bytes(MAGIC)[0];

    protected static final byte MAGIC_LOW = Bytes.short2bytes(MAGIC)[1];

    // message flag.
    private static final short NO_SUPPORT_MESSAGE = (short) 0x0000;

    private static final short HEART_BEAT_REQUEST = (short) 0x0001;

    private static final short HEART_BEAT_RESPONSE = (short) 0x0002;

    public void encode(Channel channel, ChannelBuffer buffer, Object msg) throws IOException {
        if (msg instanceof Request) {
            encodeRequest(channel, buffer, (Request) msg);
        } else if (msg instanceof Response) {
            encodeResponse(channel, buffer, (Response) msg);
        } else {
            logger.error("NO SUPPORT MESSAGE : " + msg);
            super.encode(channel, buffer, msg);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("the resulting byte size of encoding is " + buffer.readableBytes());
        }
    }

    protected void encodeRequest(Channel channel, ChannelBuffer buffer, Request req) throws IOException {
        // header.
        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);

        ByteBuffer byteBuffer = null;
        try {
            if (req.isEvent()) {
                byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);
                if (req.isHeartbeat()) {
                    Bytes.short2bytes(HEART_BEAT_REQUEST, header, 2);
                } else {
                    logger.warn("NO SUPPORT TO CLIENT EVENT REQUEST, MAYBE READONLY MESSAGE : " + req);
                    Bytes.short2bytes(NO_SUPPORT_MESSAGE, header, 2);
                }
                Bytes.int2bytes(0, header, 4);
                byteBuffer.put(header);
            } else {
                byte[] data = (byte[]) req.getData();
                int length = 0;
                if (data == null || data.length < 2) {
                    Bytes.int2bytes(NO_SUPPORT_MESSAGE, header, 2);
                    Bytes.int2bytes(length, header, 4);
                    byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);
                    byteBuffer.put(header);
                } else {
                    header[2] = data[0];
                    header[3] = data[1];
                    length = data.length - 2;
                    Bytes.int2bytes(length, header, 4);
                    byteBuffer = ByteBuffer.allocate(HEADER_LENGTH + length);
                    byteBuffer.put(header);
                    byteBuffer.put(data, 2, length);
                }
            }
            byteBuffer.flip();
            buffer.writeBytes(byteBuffer);
        } catch (Throwable t) {
            logger.error("Fail to encode request, req == " + req + ", cause: " + t.getMessage(), t);
            if (t instanceof IOException) {
                throw (IOException) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new RuntimeException(t.getMessage(), t);
            }
        } finally {
            byteBuffer.clear();
        }
    }

    protected void encodeResponse(Channel channel, ChannelBuffer buffer, Response res) throws IOException {
        // header.
        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);

        ByteBuffer byteBuffer = null;
        try {
            if (res.isEvent() && res.isHeartbeat()) {
                byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);
                Bytes.short2bytes(HEART_BEAT_RESPONSE, header, 2);
                Bytes.int2bytes(0, header, 4);
                byteBuffer.put(header);
            } else {
                RpcResult rpcResult = (RpcResult) res.getResult();
                byte[] data = (byte[]) rpcResult.getValue();
                int length = 0;
                if (data == null || data.length < 2) {
                    logger.warn("NO SUPPORT TO CLIENT EVENT REQUEST, MAYBE READONLY MESSAGE : " + res);
                    Bytes.short2bytes(NO_SUPPORT_MESSAGE, header, 2);
                    byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);
                    byteBuffer.put(header);
                } else {
                    header[2] = data[0];
                    header[3] = data[1];
                    length = data.length - 2;
                    Bytes.int2bytes(length, header, 4);
                    byteBuffer = ByteBuffer.allocate(HEADER_LENGTH + length);
                    byteBuffer.put(header);
                    byteBuffer.put(data, 2, length);
                }
            }
            byteBuffer.flip();
            buffer.writeBytes(byteBuffer);
        } catch (Throwable t) {
            logger.warn("Fail to encode response, req == " + res + ", cause: " + t.getMessage(), t);
            if (t instanceof IOException) {
                throw (IOException) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new RuntimeException(t.getMessage(), t);
            }
        }
    }

    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        int readable = buffer.readableBytes();
        byte[] header = new byte[Math.min(readable, HEADER_LENGTH)];
        buffer.readBytes(header);
        return decode(channel, buffer, readable, header);
    }

    protected Object decode(Channel channel, ChannelBuffer buffer, int readable, byte[] header) throws IOException {
        // check magic number.
        if (readable > 0 && header[0] != MAGIC_HIGH
                || readable > 1 && header[1] != MAGIC_LOW) {
            int length = header.length;
            if (header.length < readable) {
                header = Bytes.copyOf(header, readable);
                buffer.readBytes(header, length, readable - length);
            }
            for (int i = 1; i < header.length - 1; i++) {
                if (header[i] == MAGIC_HIGH && header[i + 1] == MAGIC_LOW) {
                    buffer.readerIndex(buffer.readerIndex() - header.length + i);
                    header = Bytes.copyOf(header, i);
                    break;
                }
            }
            return super.decode(channel, buffer, readable, header);
        }

        // check length.
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // get data length.
        int len = Bytes.bytes2int(header, 4);
        checkPayload(channel, len);

        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // limit input stream.
        ChannelBufferInputStream is = new ChannelBufferInputStream(buffer, len);

        try {
            return decodeBody(channel, is, header);
        } finally {
            if (is.available() > 0) {
                try {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Skip input stream " + is.available());
                    }
                    StreamUtils.skipUnusedStream(is);
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    public Object decodeBody(Channel channel, InputStream is, byte[] header) throws IOException {
        short code = Bytes.bytes2short(header, 2);
        // get heart beat code
        if (code == HEART_BEAT_REQUEST) {
            // decode heart beat request.
            Request req = new Request();
            req.setVersion("2.0.0");
            req.setTwoWay(true);
            req.setEvent(Request.HEARTBEAT_EVENT);
            return req;
        } else if (code == HEART_BEAT_RESPONSE) {
            // decode heart beat response.
            Response res = new Response();
            res.setEvent(Response.HEARTBEAT_EVENT);
            return res;
        } else {  // 其他全是请求，如果有新的需求，可以在这里增加
            // decode request.
            IDGen.get().nextId();
            Request req = new Request(IDGen.get().nextId());
            req.setVersion("2.0.0");
            req.setTwoWay(false);  // 默认返回的，也可以做成不返回的，逻辑执行成功，返回成功
            try {
                RpcInvocation inv = new RpcInvocation();
                byte[] bytes = readMessageData(is);
                URL url = channel.getUrl();
                inv.setAttachment(Constants.PATH_KEY, url.getPath());
                inv.setAttachment(Constants.INTERFACE_KEY, url.getServiceInterface());
                inv.setAttachment(Constants.BINGO_VERSION_KEY, url.getParameter(Constants.BINGO_VERSION_KEY, "2.0.0"));
                inv.setAttachment(Constants.VERSION_KEY, "0.0.0");
                inv.setMethodName(url.getParameter(Constants.METHOD_KEY, "onClientMSG").split(",")[0]);

                Class[] parameterTypes = new Class[3];
                Object[] arguments = new Object[3];

                parameterTypes[0] = int.class;
                parameterTypes[1] = byte[].class;
                parameterTypes[2] = Channel.class;

                arguments[0] = code;
                arguments[1] = bytes;
                arguments[2] = channel;

                inv.setParameterTypes(parameterTypes);
                inv.setArguments(arguments);
                req.setData(inv);
            } catch (Throwable t) {
                // bad request
                req.setBroken(true);
                req.setData(t);
            }
            return req;
        }
    }

    private byte[] readMessageData(InputStream is) throws IOException {
        if (is.available() > 0) {
            byte[] result = new byte[is.available()];
            is.read(result);
            return result;
        }
        return new byte[]{};
    }

    /**
     * 大端解码，byte to int，成对使用，（优先）
     */
    public static short bytes2IntBE(byte[] bytes) {
        if (bytes.length < 2)
            return -1;
        short iRst = (short) ((bytes[0] << 8) & 0xFF);
        iRst |= bytes[1] & 0xFF;

        return iRst;
    }

    /**
     * 大端编码，int to byte[]，成对使用，（优先）
     */
    public static byte[] inToByteBE(short i) {
        byte[] result = new byte[2];
        //由高位到低位
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 小端编码，int to byte[]，成对使用
     */
    public static byte[] intToByteLE(int i) {
        byte[] result = new byte[2];
        //由高位到低位
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 小端解码，byte to int，成对使用
     */
    public static short bytes2ShortLE(byte[] bytes) {
        if (bytes.length < 2)
            return -1;
        short iRst = (short) (bytes[0] & 0xFF);
        iRst |= (bytes[1] & 0xFF) << 8;

        return iRst;
    }


}