package com.bingo.framework.demo.invoker;

import com.bingo.framework.config.ApplicationConfig;
import com.bingo.framework.config.ProtocolConfig;
import com.bingo.framework.config.RegistryConfig;
import com.bingo.framework.config.ServiceConfig;
import com.bingo.framework.demo.hello.ClientService;
import com.bingo.framework.demo.hello.HelloService;
import com.bingo.framework.demo.hello.UserService;
import com.bingo.framework.demo.impl.ClientServiceImpl;
import com.bingo.framework.demo.impl.HelloServiceImpl;
import com.bingo.framework.demo.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ZhangGe on 2017/7/7.
 */
public class Provider {

    public static void main(String[] args) throws InterruptedException {
        // 服务实现
        HelloService helloService = new HelloServiceImpl();
        UserService userService = new UserServiceImpl();
        ClientService clientService = new ClientServiceImpl();

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("demo-provider");

        // 连接注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
        registry.setAddress("192.168.223.25:2181");

        // 服务提供者协议配置
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("bingo");
        protocol.setPort(2234);

        // 服务提供者协议配置
        ProtocolConfig protocol2 = new ProtocolConfig();
        protocol2.setName("bingo");
        protocol2.setPort(2235);

        // 客户端服务提供者协议配置
        ProtocolConfig clientProtocol = new ProtocolConfig();
        clientProtocol.setName("bingo");
        clientProtocol.setCodec("client");
        clientProtocol.setPort(1234);

        // 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口

        // 服务提供者暴露服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<HelloService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        service.setApplication(application);
        service.setRegistry(registry); // 多个注册中心可以用setRegistries()
        service.setProtocol(protocol); // 多个协议可以用setProtocols()
        service.setInterface(HelloService.class);
        service.setRef(helloService);
        //service.setLoadbalance("direct");
        service.setAsync(false);

        // 服务提供者暴露服务配置
        ServiceConfig<UserService> uService = new ServiceConfig<UserService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        uService.setApplication(application);
        uService.setRegistry(registry); // 多个注册中心可以用setRegistries()
        uService.setProtocol(protocol); // 多个协议可以用setProtocols()
        uService.setInterface(UserService.class);
        uService.setRef(userService);
        //uService.setLoadbalance("direct");
        uService.setAsync(false);

        // 服务提供者暴露服务配置
        ServiceConfig<UserService> uService2 = new ServiceConfig<UserService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        uService2.setApplication(application);
        uService2.setRegistry(registry); // 多个注册中心可以用setRegistries()
        uService2.setProtocol(protocol2); // 多个协议可以用setProtocols()
        uService2.setInterface(UserService.class);
        uService2.setRef(userService);
        uService2.setLoadbalance("direct");
        uService2.setAsync(false);

        // 服务提供者暴露服务配置
        ServiceConfig<ClientService> cService = new ServiceConfig<ClientService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        cService.setApplication(application);
        cService.setRegistry(registry); // 多个注册中心可以用setRegistries()
        cService.setProtocol(clientProtocol); // 多个协议可以用setProtocols()
        cService.setInterface(ClientService.class);
        cService.setRef(clientService);
        cService.setAsync(false);
        cService.setParameters(new HashMap<String, String>(){{
            put("onconnect", "onConnect");
            put("ondisconnect", "onDisconnect");
            put("invocation.channel", "true");
        }});

        // 暴露及注册服务
        service.export();
        uService.export();
        uService2.export();
        //cService.export();

        new CountDownLatch(1).await();
    }
}
