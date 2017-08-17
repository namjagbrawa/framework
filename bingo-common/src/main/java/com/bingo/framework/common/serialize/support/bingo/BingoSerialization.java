
package com.bingo.framework.common.serialize.support.bingo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.serialize.ObjectInput;
import com.bingo.framework.common.serialize.ObjectOutput;
import com.bingo.framework.common.serialize.Serialization;

/**
 * @author ding.lid
 */
public class BingoSerialization implements Serialization {

    public byte getContentTypeId() {
        return 1;
    }

    public String getContentType() {
        return "x-application/bingo";
    }

    public ObjectOutput serialize(URL url, OutputStream out) throws IOException {
        return new GenericObjectOutput(out);
    }

    public ObjectInput deserialize(URL url, InputStream is) throws IOException {
        return new GenericObjectInput(is);
    }

}