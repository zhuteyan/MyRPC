package com.my.rpc.model;

import com.my.rpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/***
 * 请求封装类:从反序列化来自调用者的请求得来对象中获取参数
 */
@Data   //自动生成以下代码：Getter方法，Setter方法，toString()方法，equals()和 hashCode()方法
@Builder    //生成一个构建器模式的类,允许通过链式调用设置字段值，然后通过 build()方法构建对象。
            /**例如：
             * RpcRequest request = RpcRequest.builder()
             *                                .serviceName("UserService")
             *                                .methodName("getUser")
             *                                .parameterTypes(new Class<?>[]{String.class})
             *                                .args(new Object[]{"Alice"})
             *                                .build();
             */
@AllArgsConstructor //为类生成一个全参构造器
@NoArgsConstructor  //为类生成一个全参构造器
/**
 * 当请求到达时，请求中的信息会被赋值到RpcRequest类的字段上
 * 提供方的服务程序会获取RpcRequest类，然后使用类中的字段信息通过反射调用指定的服务和方法
 * 举例如下：
 */
public class RpcRequest implements Serializable {
    /**
     * 消费者要调用提供者的哪一个服务，这是那个服务的名称
     */
    private String serviceName;

    /**
     * 消费者要调用提供者的服务的哪一个方法，这是那个方法的名称
     */
    private String methodName;

    /**
     * 服务版本
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 这是那个方法的参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 这是那个方法的形参列表
     */
    private Object[] args;
}

