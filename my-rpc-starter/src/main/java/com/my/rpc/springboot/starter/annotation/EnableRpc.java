package com.my.rpc.springboot.starter.annotation;

import com.my.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.my.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.my.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Rpc 注解，用作引入RPC框架
 *
 * @Target 指定注解可以应用于哪些元素，ElementType.TYPE表示这个注解可以被添加到一个类（通常是 Spring 的配置类）上
 * @Retention 指定注解的保留策略，RetentionPolicy.RUNTIME表示注解会在运行时被保留，可以通过反射获取到注解的值
 * @Import 用于导入配置类，这些类会被加载到上下文中，从而实现相关的功能
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     */
    boolean needServer() default true;
}