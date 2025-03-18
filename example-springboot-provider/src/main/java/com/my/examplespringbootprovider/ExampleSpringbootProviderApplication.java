package com.my.examplespringbootprovider;

import com.my.rpc.springboot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @EnableRpc 的具体作用如下：
 * 当 Spring 启动时，@EnableRpc 注解会触发以下逻辑：
 * 1.加载配置类：@Import导入的 RpcInitBootstrap、RpcProviderBootstrap 和 RpcConsumerBootstrap类会被加载
 * 2.执行初始化逻辑：这些类中的逻辑会被执行。
 */
@SpringBootApplication
@EnableRpc
public class ExampleSpringbootProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class, args);
    }

}
/**
 * 1.扫描@SpringBootApplication 标注的类及其所在包下的所有类，并进行初始化。
 * 2.加载@EnableRpc,@EnableRpc 注解通过 @Import 导入了三个类，Spring 容器会处理这些导入的类，并按照它们的类型进行相应的初始化。
 * 3.执行 RpcInitBootstrap:
 *      1)从 @EnableRpc 注解中获取 needServer 属性值
 *      2)调用 RpcApplication.init() 初始化 RPC 框架的配置和注册中心
 *      3)根据 needServer 的值决定是否启动 RPC 服务器（VertxTcpServer）
 * 4.Spring 容器初始化 Bean,包括带有 @Service、@Component 等注解的类,调用 BeanPostProcessor 的实现类来处理 Bean 的初始化前后逻辑
 * 5.执行 RpcProviderBootstrap:Spring 容器会在每个 Bean 初始化完成后调用其 postProcessAfterInitialization 方法
 *      1)检查 Bean 是否带有 @RpcService 注解。
 *      2)如果带有 @RpcService 注解，则将该服务注册到本地注册中心（LocalRegistry）和远程注册中心（通过 RegistryFactory 获取的 Registry 实例）
 * 6.执行 RpcConsumerBootstrap:Spring 容器会在每个 Bean 初始化完成后调用其 postProcessAfterInitialization 方法
 *      1)遍历 Bean 的所有字段，检查是否带有 @RpcReference 注解
 *      2)如果字段带有 @RpcReference 注解，则为该字段生成代理对象，并将代理对象注入到字段中
 * 7.所有 Bean 初始化完成后，Spring 容器启动完成，应用程序进入运行状态。如果 needServer 为 true，RPC 服务器（VertxTcpServer）已经启动，等待客户端请求
 */