/**
 * Project: bingo.registry.console-2.2.0-SNAPSHOT
 * <p>
 * File Created at Mar 21, 2012
 * $Id: RegistryServerSync.java 182143 2012-06-27 03:25:50Z tony.chenl $
 * <p>
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.bingo.framework.registry.sync;


import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.NetUtils;
import com.bingo.framework.common.utils.StringUtils;
import com.bingo.framework.registry.NotifyListener;
import com.bingo.framework.registry.RegistryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding.lid
 */
public class RegistryServerSync implements NotifyListener {

    private static final Logger logger = LoggerFactory.getLogger(RegistryServerSync.class);

    private static final String PROVIDERS = "providers";

    private static final String CONSUMERS = "consumers";

    private RegistryService registryService;

    private static final URL SUBSCRIBE = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "",
            Constants.INTERFACE_KEY, Constants.ANY_VALUE,
            Constants.GROUP_KEY, Constants.ANY_VALUE,
            Constants.VERSION_KEY, Constants.ANY_VALUE,
            Constants.CLASSIFIER_KEY, Constants.ANY_VALUE,
            Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY + ","
            + Constants.CONSUMERS_CATEGORY + ","
            + Constants.ROUTERS_CATEGORY + ","
            + Constants.CONFIGURATORS_CATEGORY,
            Constants.ENABLED_KEY, Constants.ANY_VALUE,
            Constants.CHECK_KEY, String.valueOf(false));

    private static final AtomicLong ID = new AtomicLong();

    // ConcurrentMap<category, ConcurrentMap<servicename, Map<Long, URL>>>
    private static final ConcurrentMap<String, ConcurrentMap<String, Map<Long, URL>>> registryCache = new ConcurrentHashMap<String, ConcurrentMap<String, Map<Long, URL>>>();

    // 获取providers
    public static List<URL> getProviders(String serviceName) {
        return get(serviceName, PROVIDERS);
    }

    // 获取consumers
    public static List<URL> getConsumers(String serviceName) {
        return get(serviceName, CONSUMERS);
    }

    // 获取引用
    public static List<URL> get(String serviceName, String type) {
        List<URL> result = new ArrayList<>();
        if (StringUtils.isBlank(serviceName)) {
            return result;
        }
        for (Map.Entry<String, ConcurrentMap<String, Map<Long, URL>>> catalog : registryCache.entrySet()) {
            if (catalog.getKey().equals(type)) {
                for (Map.Entry<String, Map<Long, URL>> service : catalog.getValue().entrySet()) {
                    if (service.getKey().equals(serviceName)) {
                        for (Map.Entry<Long, URL> entry : service.getValue().entrySet()) {
                            result.add(entry.getValue());
                        }
                    }
                }
            }
        }
        return result;
    }

    public ConcurrentMap<String, ConcurrentMap<String, Map<Long, URL>>> getRegistryCache() {
        return registryCache;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
        afterPropertiesSet();
    }

    public void afterPropertiesSet() {
        logger.info("Init Bingo Registry Sync Cache...");
        registryService.subscribe(SUBSCRIBE, this);
    }

    public void destroy() throws Exception {
        registryService.unsubscribe(SUBSCRIBE, this);
    }

    // 收到的通知对于 ，同一种类型数据（override、subcribe、route、其它是Provider），同一个服务的数据是全量的
    public void notify(List<URL> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        // Map<category, Map<servicename, Map<Long, URL>>>
        final Map<String, Map<String, Map<Long, URL>>> categories = new HashMap<String, Map<String, Map<Long, URL>>>();
        for (URL url : urls) {
            String category = url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY);
            if (Constants.EMPTY_PROTOCOL.equalsIgnoreCase(url.getProtocol())) { // 注意：empty协议的group和version为*
                ConcurrentMap<String, Map<Long, URL>> services = registryCache.get(category);
                if (services != null) {
                    String group = url.getParameter(Constants.GROUP_KEY);
                    String version = url.getParameter(Constants.VERSION_KEY);
                    // 注意：empty协议的group和version为*
                    if (!Constants.ANY_VALUE.equals(group) && !Constants.ANY_VALUE.equals(version)) {
                        services.remove(url.getServiceKey());
                    } else {
                        for (Map.Entry<String, Map<Long, URL>> serviceEntry : services.entrySet()) {
                            String service = serviceEntry.getKey();
                            if (Tool.getInterface(service).equals(url.getServiceInterface())
                                    && (Constants.ANY_VALUE.equals(group) || StringUtils.isEquals(group, Tool.getGroup(service)))
                                    && (Constants.ANY_VALUE.equals(version) || StringUtils.isEquals(version, Tool.getVersion(service)))) {
                                services.remove(service);
                            }
                        }
                    }
                }
            } else {
                Map<String, Map<Long, URL>> services = categories.get(category);
                if (services == null) {
                    services = new HashMap<String, Map<Long, URL>>();
                    categories.put(category, services);
                }
                String service = url.getServiceKey();
                Map<Long, URL> ids = services.get(service);
                if (ids == null) {
                    ids = new HashMap<Long, URL>();
                    services.put(service, ids);
                }
                ids.put(ID.incrementAndGet(), url);
            }
        }
        for (Map.Entry<String, Map<String, Map<Long, URL>>> categoryEntry : categories.entrySet()) {
            String category = categoryEntry.getKey();
            ConcurrentMap<String, Map<Long, URL>> services = registryCache.get(category);
            if (services == null) {
                services = new ConcurrentHashMap<String, Map<Long, URL>>();
                registryCache.put(category, services);
            }
            services.putAll(categoryEntry.getValue());
        }
    }
}
    
