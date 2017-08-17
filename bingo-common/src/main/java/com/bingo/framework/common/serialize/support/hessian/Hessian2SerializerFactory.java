package com.bingo.framework.common.serialize.support.hessian;


import com.bingo.hessian.io.SerializerFactory;

public class Hessian2SerializerFactory extends SerializerFactory {

	public static final SerializerFactory SERIALIZER_FACTORY = new Hessian2SerializerFactory();

	private Hessian2SerializerFactory() {
	}

	@Override
	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

}
