
package com.bingo.framework.common.serialize.support.java;

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
public class CompactedJavaSerialization implements Serialization {

    public byte getContentTypeId() {
        return 4;
    }

    public String getContentType() {
        return "x-application/compactedjava";
    }

    public ObjectOutput serialize(URL url, OutputStream out) throws IOException {
        return new JavaObjectOutput(out, true);
    }

    public ObjectInput deserialize(URL url, InputStream is) throws IOException {
        return new JavaObjectInput(is, true);
    }

}