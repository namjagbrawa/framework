/*
 * Copyright 1999-2011 Alibaba Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.bingo.framework.rpc.protocol.bingo;

import java.io.IOException;

import com.bingo.framework.common.Constants;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.Codec2;
import com.bingo.framework.remoting.buffer.ChannelBuffer;
import com.bingo.framework.remoting.exchange.Request;
import com.bingo.framework.remoting.exchange.Response;
import com.bingo.framework.remoting.exchange.support.MultiMessage;
import com.bingo.framework.rpc.RpcInvocation;
import com.bingo.framework.rpc.RpcResult;

public final class BingoCountCodec implements Codec2 {

    private BingoCodec codec = new BingoCodec();

    public void encode(Channel channel, ChannelBuffer buffer, Object msg) throws IOException {
        codec.encode(channel, buffer, msg);
    }

    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        int save = buffer.readerIndex();
        MultiMessage result = MultiMessage.create();
        do {
            Object obj = codec.decode(channel, buffer);
            if (Codec2.DecodeResult.NEED_MORE_INPUT == obj) {
                buffer.readerIndex(save);
                break;
            } else {
                result.addMessage(obj);
                logMessageLength(obj, buffer.readerIndex() - save);
                save = buffer.readerIndex();
            }
        } while (true);
        if (result.isEmpty()) {
            return Codec2.DecodeResult.NEED_MORE_INPUT;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        return result;
    }

    private void logMessageLength(Object result, int bytes) {
        if (bytes <= 0) { return; }
        if (result instanceof Request) {
            try {
                ((RpcInvocation) ((Request) result).getData()).setAttachment(
                    Constants.INPUT_KEY, String.valueOf(bytes));
            } catch (Throwable e) {
                /* ignore */
            }
        } else if (result instanceof Response) {
            try {
                ((RpcResult) ((Response) result).getResult()).setAttachment(
                    Constants.OUTPUT_KEY, String.valueOf(bytes));
            } catch (Throwable e) {
                /* ignore */
            }
        }
    }

}
