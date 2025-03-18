package com.my.rpc.springboot.starter.bootstrap;

import com.my.rpc.proxy.ServiceProxyFactory;
import com.my.rpc.springboot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 扫描 Spring 容器中的所有 Bean：在每个 Bean 初始化完成后，检查其字段。
 * 查找带有 @RpcReference 注解的字段：这些字段需要注入 RPC 服务的代理对象。
 * 生成代理对象：通过 ServiceProxyFactory.getProxy 方法生成代理对象。
 * 注入代理对象：将代理对象注入到字段中，使得字段可以代理对远程服务的调用。
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     *BeanPostProcessor 是 Spring 提供的一个接口。它有两个方法：
     * postProcessBeforeInitialization：在 Bean 初始化之前执行。
     * postProcessAfterInitialization：在 Bean 初始化之后执行。
     * RpcConsumerBootstrap 实现了 postProcessAfterInitialization 方法，这意味着它会在每个 Bean 初始化完成后执行逻辑。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 遍历对象的所有字段
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //检查字段是否带有 @RpcReference 注解
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {//如果注解中未指定接口类（void.class 是占位符），则使用字段的类型作为接口类
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);// 打开私有字段的访问权限
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);// 将代理对象注入到字段
                    field.setAccessible(false);// 恢复私有字段的访问权限
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}