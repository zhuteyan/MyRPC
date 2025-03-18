package com.my.rpc.registry;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 * 使用线程安全的 ConcurrentHashMap 存储服务注册信息，key 为服务名称、value 为服务的实现类。
 * 之后就可以根据服务名称获取到对应的实现类，然后通过反射进行方法调用了。
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     * 在实现本地注册中心时，将服务的实现类存储为 Class<?> 而不是直接存储服务实例（对象）是一个常见的设计选择
     *
     * 好处：
     * 1、存储 Class<?>意味着服务实例只有在真正需要时才会被创建，而不是在注册时就立即实例化。
     * 示例：
     *      map.put("serviceName", MyService.class);
     *      // 在调用时才实例化
     *      Class<?> clazz = map.get("serviceName");
     *      Object serviceInstance = clazz.getDeclaredConstructor().newInstance();
     * 2、存储 Class<?> 允许在实例化时采用不同的策略，例如：单例模式、多实例模式
     * 示例：
     *      Class<?> clazz = map.get("serviceName");
     *      // 单例模式
     *      MyService singletonInstance = clazz.getDeclaredConstructor().newInstance();
     *      // 多实例模式
     *      MyService instance1 = clazz.getDeclaredConstructor().newInstance();
     *      MyService instance2 = clazz.getDeclaredConstructor().newInstance();
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}
