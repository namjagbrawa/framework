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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.serialize.Cleanable;
import com.bingo.framework.common.serialize.ObjectInput;
import com.bingo.framework.common.utils.Assert;
import com.bingo.framework.common.utils.ReflectUtils;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.remoting.Channel;
import com.bingo.framework.remoting.Codec;
import com.bingo.framework.remoting.Decodeable;
import com.bingo.framework.remoting.exchange.Request;
import com.bingo.framework.remoting.transport.CodecSupport;
import com.bingo.framework.rpc.RpcInvocation;

import static com.bingo.framework.rpc.protocol.bingo.CallbackServiceCodec.decodeInvocationArgument;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
public class DecodeableRpcInvocation extends RpcInvocation implements Codec, Decodeable {

    private static final Logger log = LoggerFactory.getLogger(DecodeableRpcInvocation.class);

    private Channel     channel;

    private byte        serializationType;

    private InputStream inputStream;

    private Request     request;

    private volatile boolean hasDecoded;

    public DecodeableRpcInvocation(Channel channel, Request request, InputStream is, byte id) {
        Assert.notNull(channel, "channel == null");
        Assert.notNull(request, "request == null");
        Assert.notNull(is, "inputStream == null");
        this.channel = channel;
        this.request = request;
        this.inputStream = is;
        this.serializationType = id;
    }

    public void decode() throws Exception {
        if (!hasDecoded && channel != null && inputStream != null) {
            try {
                decode(channel, inputStream);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("Decode rpc invocation failed: " + e.getMessage(), e);
                }
                request.setBroken(true);
                request.setData(e);
            } finally {
                hasDecoded = true;
            }
        }
    }

    public void encode(Channel channel, OutputStream output, Object message) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Object decode(Channel channel, InputStream input) throws IOException {
        ObjectInput in = CodecSupport.getSerialization(channel.getUrl(), serializationType)
                .deserialize(channel.getUrl(), input);

        try {
            setAttachment(Constants.BINGO_VERSION_KEY, in.readUTF());
            setAttachment(Constants.PATH_KEY, in.readUTF());
            setAttachment(Constants.VERSION_KEY, in.readUTF());

            setMethodName(in.readUTF());
            try {
                Object[] args;
                Class<?>[] pts;

                // NOTICE modified by lishen
                int argNum = in.readInt();
                if (argNum >= 0) {
                    if (argNum == 0) {
                        pts = BingoCodec.EMPTY_CLASS_ARRAY;
                        args = BingoCodec.EMPTY_OBJECT_ARRAY;
                    } else {
                        args = new Object[argNum];
                        pts = new Class[argNum];
                        for (int i = 0; i < args.length; i++) {
                            try {
                                args[i] = in.readObject();
                                pts[i] = args[i].getClass();
                            } catch (Exception e) {
                                if (log.isWarnEnabled()) {
                                    log.warn("Decode argument failed: " + e.getMessage(), e);
                                }
                            }
                        }
                    }
                } else {
                    String desc = in.readUTF();
                    if (desc.length() == 0) {
                        pts = BingoCodec.EMPTY_CLASS_ARRAY;
                        args = BingoCodec.EMPTY_OBJECT_ARRAY;
                    } else {
                        pts = ReflectUtils.desc2classArray(desc);
                        args = new Object[pts.length];
                        for (int i = 0; i < args.length; i++) {
                            try {
                                args[i] = in.readObject(pts[i]);
                            } catch (Exception e) {
                                if (log.isWarnEnabled()) {
                                    log.warn("Decode argument failed: " + e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
                setParameterTypes(pts);

                Map<String, String> map = (Map<String, String>) in.readObject(Map.class);
                if (map != null && map.size() > 0) {
                    Map<String, String> attachment = getAttachments();
                    if (attachment == null) {
                        attachment = new HashMap<String, String>();
                    }
                    attachment.putAll(map);
                    setAttachments(attachment);
                }
                //decode argument ,may be callback
                for (int i = 0; i < args.length; i++) {
                    args[i] = decodeInvocationArgument(channel, this, pts, i, args[i]);
                }

                setArguments(args);

            } catch (ClassNotFoundException e) {
                throw new IOException(StringUtils.toString("Read invocation data failed.", e));
            }
        } finally {
            // modified by lishen
            if (in instanceof Cleanable) {
                ((Cleanable) in).cleanup();
            }
        }
        return this;
    }

}
