
package com.bingo.framework.common.compiler.support;


import com.bingo.framework.common.compiler.Compiler;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.ExtensionLoader;

/**
 * AdaptiveCompiler. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@Adaptive
public class AdaptiveCompiler implements Compiler {

    private static volatile String DEFAULT_COMPILER;

    public static void setDefaultCompiler(String compiler) {
        DEFAULT_COMPILER = compiler;
    }

    public Class<?> compile(String code, ClassLoader classLoader) {
        Compiler compiler;
        ExtensionLoader<Compiler> loader = ExtensionLoader.getExtensionLoader(Compiler.class);
        String name = DEFAULT_COMPILER; // copy reference
        if (name != null && name.length() > 0) {
            compiler = loader.getExtension(name);
        } else {
            compiler = loader.getDefaultExtension();
        }
        return compiler.compile(code, classLoader);
    }

}
