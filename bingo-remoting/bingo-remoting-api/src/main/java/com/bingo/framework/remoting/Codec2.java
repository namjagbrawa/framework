package com.bingo.framework.remoting;

import java.io.IOException;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;
import com.bingo.framework.remoting.buffer.ChannelBuffer;

/**
 * @author <a href="mailto:gang.lvg@taobao.com">kimi</a>
 */
@SPI
public interface Codec2 {

    @Adaptive({Constants.CODEC_KEY})
    void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException;

    @Adaptive({Constants.CODEC_KEY})
    Object decode(Channel channel, ChannelBuffer buffer) throws IOException;


    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_SOME_INPUT
    }

}

