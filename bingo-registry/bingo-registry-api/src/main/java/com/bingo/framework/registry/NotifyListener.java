package com.bingo.framework.registry;

import java.util.List;

import com.bingo.framework.common.URL;

/**
 * NotifyListener. (API, Prototype, ThreadSafe)
 * 
 * @see RegistryService#subscribe(URL, NotifyListener)
 * @author william.liangf
 */
public interface NotifyListener {

    /**
     * 当收到服务变更通知时触发。
     * 
     * 通知需处理契约：<br>
     * 1. 总是以服务接口和数据类型为维度全量通知，即不会通知一个服务的同类型的部分数据，用户不需要对比上一次通知结果。<br>
     * 2. 订阅时的第一次通知，必须是一个服务的所有类型数据的全量通知。<br>
     * 3. 中途变更时，允许不同类型的数据分开通知，比如：providers, consumers, routers, overrides，允许只通知其中一种类型，但该类型的数据必须是全量的，不是增量的。<br>
     * 4. 如果一种类型的数据为空，需通知一个empty协议并带category参数的标识性URL数据。<br>
     * 5. 通知者(即注册中心实现)需保证通知的顺序，比如：单线程推送，队列串行化，带版本对比。<br>
     * 
     * @param urls 已注册信息列表，总不为空，含义同{@link RegistryService#lookup(URL)}的返回值。
     */
    void notify(List<URL> urls);

}