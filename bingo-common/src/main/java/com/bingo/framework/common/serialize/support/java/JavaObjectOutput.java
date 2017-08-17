
package com.bingo.framework.common.serialize.support.java;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.bingo.framework.common.serialize.support.nativejava.NativeJavaObjectOutput;

/**
 * Java Object output.
 * 
 * @author qian.lei
 */

public class JavaObjectOutput extends NativeJavaObjectOutput
{
	public JavaObjectOutput(OutputStream os) throws IOException
	{
		super(new ObjectOutputStream(os));
	}

	public JavaObjectOutput(OutputStream os, boolean compact) throws IOException
	{
		super(compact ? new CompactedObjectOutputStream(os) : new ObjectOutputStream(os));
	}

	public void writeUTF(String v) throws IOException
	{
		if( v == null )
		{
			getObjectOutputStream().writeInt(-1);
		}
		else
		{
			getObjectOutputStream().writeInt(v.length());
			getObjectOutputStream().writeUTF(v);
		}
	}

	public void writeObject(Object obj) throws IOException
	{
		if( obj == null )
		{
			getObjectOutputStream().writeByte(0);
		}
		else
		{
			getObjectOutputStream().writeByte(1);
			getObjectOutputStream().writeObject(obj);
		}
	}

	public void flushBuffer() throws IOException
	{
		getObjectOutputStream().flush();
	}
}