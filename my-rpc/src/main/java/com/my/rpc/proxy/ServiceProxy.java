package com.my.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.my.rpc.RpcApplication;
import com.my.rpc.config.RpcConfig;
import com.my.rpc.constant.RpcConstant;
import com.my.rpc.fault.retry.RetryStrategy;
import com.my.rpc.fault.retry.RetryStrategyFactory;
import com.my.rpc.fault.tolerant.TolerantStrategy;
import com.my.rpc.fault.tolerant.TolerantStrategyFactory;
import com.my.rpc.loadbalancer.LoadBalancer;
import com.my.rpc.loadbalancer.LoadBalancerFactory;
import com.my.rpc.model.RpcRequest;
import com.my.rpc.model.RpcResponse;
import com.my.rpc.model.ServiceMetaInfo;
import com.my.rpc.registry.Registry;
import com.my.rpc.registry.RegistryFactory;
import com.my.rpc.serializer.Serializer;
import com.my.rpc.serializer.SerializerFactory;
import com.my.rpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造RpcRequest
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 获取注册中心实例
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        // 构造服务元信息中的ServiceKey(serviceName:serviceVersion)
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        /**
         * 根据serviceKey从注册中心中获取对应的serviceMetaInfo
         * 注册中心中存放的是serviceKey:serviceMetaInfo
         */
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        /**
         * 用客户端发TCP请求
         * doRequest()方法传入ServiceMetaInfo作为参数是因为要用到ServiceMetaInfo中的地址和端口字段
         * 重试机制
         */
        RpcResponse rpcResponse;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }
}