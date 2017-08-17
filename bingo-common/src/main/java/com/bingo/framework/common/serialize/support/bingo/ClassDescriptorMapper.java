
package com.bingo.framework.common.serialize.support.bingo;

public interface ClassDescriptorMapper
{
	/**
	 * get Class-Descriptor by index.
	 * 
	 * @param index index.
	 * @return string.
	 */
	String getDescriptor(int index);

	/**
	 * get Class-Descriptor index
	 * 
	 * @param desc Class-Descriptor
	 * @return index.
	 */
	int getDescriptorIndex(String desc);
}