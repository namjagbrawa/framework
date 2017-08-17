package com.bingo.framework.demo.client;

/**
 * Created by ZhangGe on 2017/7/10.
 */
public class ClientMessage {

    public static final short magic = (short) 0xACCA;
    public static final short hbreq = (short) 0x0001;
    public static final short hbres = (short) 0x0002;
    public static final short no = (short) 0x0000;

    public ClientMessage(int code, byte[] bytes) {
        this.code = code;
        this.bytes = bytes;
    }

    int code;

    byte[] bytes;

    public int getCode() {
        return code;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
