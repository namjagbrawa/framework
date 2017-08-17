
package com.bingo.framework.common.compiler;


import com.bingo.framework.common.extension.SPI;

/**
 * Compiler. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI("javassist")
public interface Compiler {

	/**
	 * Compile java source code.
	 * 
	 * @param code Java source code
	 * @param classLoader TODO
	 * @return Compiled class
	 */
	Class<?> compile(String code, ClassLoader classLoader);

}
