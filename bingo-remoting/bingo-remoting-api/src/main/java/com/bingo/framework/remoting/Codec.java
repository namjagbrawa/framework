package com.bingo.framework.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;

/**
 * Codec. (SPI, Singleton, ThreadSafe)
 * 
 * @author qianlei
 * @author ding.lid
 * @author william.liangf
 */
@Deprecated
@SPI
public interface Codec {

	/**
	 * Need more input poison.
	 * 
	 * @see #decode(Channel, InputStream)
	 */
	Object NEED_MORE_INPUT = new Object();

    /**
     * Encode message.
     * 
     * @param channel channel.
     * @param output output stream.
     * @param message message.
     */
	@Adaptive({Constants.CODEC_KEY})
    void encode(Channel channel, OutputStream output, Object message) throws IOException;

	/**
	 * Decode message.
	 * 
	 * @see #NEED_MORE_INPUT
	 * @param channel channel.
	 * @param input input stream.
	 * @return message or <code>NEED_MORE_INPUT</code> poison.
	 */
    @Adaptive({Constants.CODEC_KEY})
	Object decode(Channel channel, InputStream input) throws IOException;

}