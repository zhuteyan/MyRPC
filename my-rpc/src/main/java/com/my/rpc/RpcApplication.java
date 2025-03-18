package com.my.rpc;


import cn.hutool.setting.dialect.Props;
import com.my.rpc.config.RegistryConfig;
import com.my.rpc.config.RpcConfig;
import com.my.rpc.constant.RpcConstant;
import com.my.rpc.registry.Registry;
import com.my.rpc.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架
 */
@Slf4j
public class RpcApplication {
    //当一个线程修改了 volatile 变量的值时，其他线程能够立即看到这个修改。
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            //工具类ConfigUtils:其静态方法会读取配置文件,返回配置封装类RpcConfig的实例
            newRpcConfig = loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //使用默认值
            newRpcConfig = new RpcConfig();
        }
        rpcConfig = newRpcConfig;
        log.info("rpc初始化: RpcConfig = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("注册中心初始化, registryConfig = {}", registryConfig);

        // 注册 Shutdown Hook，程序正常退出时会执行注册中心的 destroy 方法。
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 获取配置
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

    /**
     * 读取配置文件,返回RpcConfig的实例
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix){
        Props props = new Props("application.properties");
        //将配置文件中的键值对映射到一个类中，如果 prefix是 "app."，则只有以 "app."开头的键会被映射到目标类的字段中
        return props.toBean(tClass, prefix);
    }
}
