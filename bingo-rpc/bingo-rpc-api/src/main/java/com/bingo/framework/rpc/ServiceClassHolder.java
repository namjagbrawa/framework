package com.bingo.framework.rpc;

/**
 * TODO this is just a workround for rest protocol, and now we just ensure it works in the most common bingo usages
 *
 * @author lishen
 */
public class ServiceClassHolder {

    private static final ServiceClassHolder INSTANCE = new ServiceClassHolder();

    private final ThreadLocal<Class> holder  = new ThreadLocal<Class>();

    public static ServiceClassHolder getInstance() {
        return INSTANCE;
    }

    private ServiceClassHolder() {
    }

    public Class popServiceClass() {
        Class clazz = holder.get();
        holder.remove();
        return clazz;
    }

    public void pushServiceClass(Class clazz) {
        holder.set(clazz);
    }
}
