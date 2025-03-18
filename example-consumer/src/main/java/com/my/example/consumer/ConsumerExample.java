package com.my.example.consumer;

import com.my.example.common.model.User;
import com.my.example.common.service.UserService;
import com.my.rpc.bootstrap.ConsumerBootstrap;
import com.my.rpc.proxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理，代理工厂可以生产不同类型的代理，生产的是哪个代理，通过该代理调用接口的任何方法走的都是该代理的invoke()方法
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        // 调用，在代理的invoke()方法里，实现了对接口方法getUser(user)的调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
