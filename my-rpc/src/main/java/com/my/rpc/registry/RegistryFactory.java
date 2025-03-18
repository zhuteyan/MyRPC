package com.my.rpc.registry;

import com.my.rpc.spi.SpiLoader;

/**
 * 注册中心工厂（用于实现注册中心接口Registry）
 */
public class RegistryFactory {

    static {//初始化SpiLoader类中的两个Map字段，以便getInstance能直接从Map中拿值
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}