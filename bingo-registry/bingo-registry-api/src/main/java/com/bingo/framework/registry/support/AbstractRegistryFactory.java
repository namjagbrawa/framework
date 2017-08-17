package com.bingo.framework.registry.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.registry.Registry;
import com.bingo.framework.registry.RegistryFactory;
import com.bingo.framework.registry.RegistryService;

/**
 * AbstractRegistryFactory. (SPI, Singleton, ThreadSafe)
 * 
 * @see RegistryFactory
 * @author william.liangf
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    // 日志输出
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegistryFactory.class);

    // 注册中心获取过程锁
    private static final ReentrantLock LOCK = new ReentrantLock();

    // 注册中心集合 Map<RegistryAddress, Registry>
    private static final Map<String, Registry> REGISTRIES = new ConcurrentHashMap<String, Registry>();

    /**
     * 获取所有注册中心
     * 
     * @return 所有注册中心
     */
    public static Collection<Registry> getRegistries() {
        return Collections.unmodifiableCollection(REGISTRIES.values());
    }

    /**
     * 关闭所有已创建注册中心
     */
    public static void destroyAll() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Close all registries " + getRegistries());
        }
        // 锁定注册中心关闭过程
        LOCK.lock();
        try {
            for (Registry registry : getRegistries()) {
                try {
                    registry.destroy();
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            REGISTRIES.clear();
        } finally {
            // 释放锁
            LOCK.unlock();
        }
    }

    public Registry getRegistry(URL url) {
    	url = url.setPath(RegistryService.class.getName())
    			.addParameter(Constants.INTERFACE_KEY, RegistryService.class.getName())
    			.removeParameters(Constants.EXPORT_KEY, Constants.REFER_KEY);
    	String key = url.toServiceString();
        // 锁定注册中心获取过程，保证注册中心单一实例
        LOCK.lock();
        try {
            Registry registry = REGISTRIES.get(key);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(url);
            if (registry == null) {
                throw new IllegalStateException("Can not create registry " + url);
            }
            REGISTRIES.put(key, registry);
            return registry;
        } finally {
            // 释放锁
            LOCK.unlock();
        }
    }

    protected abstract Registry createRegistry(URL url);

}