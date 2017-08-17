
package com.bingo.framework.common.serialize.support.bingo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bingo.framework.common.serialize.ObjectOutput;
import com.bingo.framework.common.utils.ReflectUtils;

/**
 * Generic Object Output.
 * 
 * @author qian.lei
 */

public class GenericObjectOutput extends GenericDataOutput implements ObjectOutput
{
	private ClassDescriptorMapper mMapper;

	private Map<Object, Integer> mRefs = new ConcurrentHashMap<Object, Integer>();
	
	private final boolean isAllowNonSerializable;

	public GenericObjectOutput(OutputStream out)
	{
		this(out, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER);
	}

	public GenericObjectOutput(OutputStream out, ClassDescriptorMapper mapper)
	{
		super(out);
		mMapper = mapper;
		isAllowNonSerializable = false;
	}

	public GenericObjectOutput(OutputStream out, int buffSize)
	{
		this(out, buffSize, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER, false);
	}

	public GenericObjectOutput(OutputStream out, int buffSize, ClassDescriptorMapper mapper)
	{
		this(out, buffSize, mapper, false);
	}
	
	public GenericObjectOutput(OutputStream out, int buffSize, ClassDescriptorMapper mapper, boolean isAllowNonSerializable)
	{
	    super(out, buffSize);
	    mMapper = mapper;
	    this.isAllowNonSerializable = isAllowNonSerializable;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeObject(Object obj) throws IOException
	{
		if( obj == null )
		{
			write0(OBJECT_NULL);
			return;
		}
		
		Class<?> c = obj.getClass();
		if( c == Object.class )
		{
			write0(OBJECT_DUMMY);
		}
		else
		{
			String desc = ReflectUtils.getDesc(c);
			int index = mMapper.getDescriptorIndex(desc);
			if( index < 0 )
			{
				write0(OBJECT_DESC);
				writeUTF(desc);
			}
			else
			{
				write0(OBJECT_DESC_ID);
				writeUInt(index);
			}
			Builder b = Builder.register(c, isAllowNonSerializable);
			b.writeTo(obj, this);
		}
	}

	public void addRef(Object obj)
	{
		mRefs.put(obj, mRefs.size());
	}

	public int getRef(Object obj)
	{
		Integer ref = mRefs.get(obj);
		if( ref == null )
			return -1;
		return ref.intValue();
	}
}