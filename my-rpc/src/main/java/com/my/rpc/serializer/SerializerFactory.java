package com.my.rpc.serializer;

import com.my.rpc.spi.SpiLoader;


/**
 * 序列化器工厂（用于获取序列化器对象）
 */
public class SerializerFactory {
    /**
     * 静态代码块,在类加载到JVM时自动执行一次，用于向SpiLoader的静态字段loaderMap和instanceCache中存数据
     * 首次加载时，调用 SpiLoader的load方法加载指定接口的所有实现类
     * 之后就可以通过调用 getInstance 方法获取指定的实现类对象了
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}