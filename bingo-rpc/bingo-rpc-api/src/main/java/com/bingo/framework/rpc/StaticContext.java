package com.bingo.framework.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bingo.framework.common.URL;
import com.bingo.framework.common.utils.StringUtils;

/**
 * 系统存储，内部类.
 */
public class StaticContext extends ConcurrentHashMap<Object, Object>{
    private static final long serialVersionUID = 1L;
    private static final String SYSTEMNAME = "system";
    private String name ;
	
	private static final ConcurrentMap<String, StaticContext> context_map = new ConcurrentHashMap<String, StaticContext>() ;
	
    private StaticContext(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static StaticContext getSystemContext() {
        return getContext(SYSTEMNAME);
    }

    public static StaticContext getContext(String name) {
	    StaticContext appContext = context_map.get(name);
	    if (appContext == null){
	        appContext = context_map.putIfAbsent(name, new StaticContext(name));
	        if (appContext == null){
	            appContext = context_map.get(name);
	        }
	    }
	    return appContext;
	}
    public static StaticContext remove(String name){
        return context_map.remove(name);
    }
    
    public static String getKey(URL url, String methodName, String suffix) {
        return getKey(url.getServiceKey(), methodName, suffix);
    }
    public static String getKey(Map<String, String> paras, String methodName, String suffix) {
        return getKey(StringUtils.getServiceKey(paras), methodName, suffix);
    }
    private static String getKey(String servicekey, String methodName, String suffix) {
        StringBuffer sb = new StringBuffer().append(servicekey).append(".").append(methodName).append(".").append(suffix);
        return sb.toString();
    }
}