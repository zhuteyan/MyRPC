package com.my.rpc.proxy;

import com.my.rpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        //创建ServiceProxy代理
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                //new ServiceProxy()表示通过Proxy.newProxyInstance()创建的代理调用接口的方法时，会调用ServiceProxy的invoke()方法
                new ServiceProxy());
    }

    /**
     * 根据服务类获取 Mock 代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        //创建MockServiceProxy代理
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                //new MockServiceProxy()表示通过Proxy.newProxyInstance()创建的代理调用接口的方法时，会调用MockServiceProxy的invoke()方法
                new MockServiceProxy());
    }
}
