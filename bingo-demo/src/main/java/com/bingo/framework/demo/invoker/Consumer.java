package com.bingo.framework.demo.invoker;

import com.bingo.framework.config.ApplicationConfig;
import com.bingo.framework.config.ReferenceConfig;
import com.bingo.framework.config.RegistryConfig;
import com.bingo.framework.demo.bean.User;
import com.bingo.framework.demo.hello.HelloService;
import com.bingo.framework.demo.hello.UserService;
import com.bingo.framework.rpc.Exporter;
import com.bingo.framework.rpc.protocol.bingo.BingoProtocol;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ZhangGe on 2017/7/7.
 */
public class Consumer {

    public static void main(String[] args) {
        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("demo-consumer");

        // 连接注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
        registry.setAddress("192.168.223.25:2181");

        // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接

        /*// 引用远程服务
        ReferenceConfig<HelloService> reference = new ReferenceConfig<HelloService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
        reference.setInterface(HelloService.class);
        reference.setTimeout(5000);

        // 和本地bean一样使用xxxService
        HelloService helloService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用

        for (int i = 0; i < 1000; i ++) {
            String hello = helloService.hello(String.valueOf(i));
            System.out.println(hello);
        }*/

        // 引用远程服务
        ReferenceConfig<UserService> reference = new ReferenceConfig<UserService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
        reference.setInterface(UserService.class);
        reference.setTimeout(5000);

        // 和本地bean一样使用xxxService
        UserService UserService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用

        for (int i = 0; i < 1000; i++) {
            Long user = UserService.a(new ArrayList<Long>() {{
                add(892296276316520448L);
                add(892296360768831488L);
            }});
            System.out.println(user);
        }

    }
}
