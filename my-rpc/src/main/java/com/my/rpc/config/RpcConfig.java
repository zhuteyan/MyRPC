package com.my.rpc.config;

import com.my.rpc.fault.retry.RetryStrategyKeys;
import com.my.rpc.fault.tolerant.TolerantStrategyKeys;
import com.my.rpc.loadbalancer.LoadBalancerKeys;
import com.my.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC框架的配置信息封装类
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "my-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     * 对于布尔类型的字段，Lombok 会生成一个特殊的 Getter 方法，
     * 命名为 is<FieldName>()，用于获取 mock 字段的值。
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

}
