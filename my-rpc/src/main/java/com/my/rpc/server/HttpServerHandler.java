package com.my.rpc.server;

import com.my.rpc.RpcApplication;
import com.my.rpc.model.RpcRequest;
import com.my.rpc.model.RpcResponse;
import com.my.rpc.registry.LocalRegistry;
import com.my.rpc.serializer.Serializer;
import com.my.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;
/**
 * 处理来自调用者的请求，业务流程如下：
 * 1. 反序列化请求为对象，并从对象中获取参数。
 * 2. 根据服务名称从本地注册器中获取到对应的服务实现类的Class。
 * 3. 通过反射机制调用方法，得到返回结果。
 * 4. 对返回结果进行封装和序列化，并写入到响应中。
 */
//Handler<T>是Vert.x中的一个核心接口，定义了一个简单的回调机制,T表示事件的类型
//HttpServerRequest是Vert.x中的一个类，表示一个 HTTP 请求,它提供了对请求的各种操作，例如：
//获取请求的 URI、方法、头信息等。
//读取请求体。
//发送响应。
//回调机制：将一个函数（或方法）作为参数传递给另一个函数（或方法），并在适当的时候调用它
public class HttpServerHandler implements Handler<HttpServerRequest>{
    @Override
    /**
     * 当客户端请求到 Vert.x 的 HTTP 服务器时，会触发事件，Vert.x框架会负责接收这个请求，并创建一个HttpServerRequest实例，
     * 并将其传递给 HttpServerHandler的 handle方法
     */
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //记录日志
        System.out.println("Received request: "+request.method()+""+request.uri());
        //异步处理http请求,body 是一个 Buffer 对象，包含了完整的请求体数据。
        request.bodyHandler(body -> {
            byte[] bytes=body.getBytes();//将 Buffer对象转换为字节数组，用于后续的反序列化操作
            RpcRequest rpcRequest=null;
            try{
                rpcRequest=serializer.deserialize(bytes, RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为null，直接返回
            if(rpcRequest==null ){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }
            try{
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request, rpcResponse, serializer);
        });
    }
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try{
            byte[] serialized = serializer.serialize(rpcResponse);
            //HttpServerResponse 对象是与当前 HTTP 请求关联的响应对象，它会自动发送给发起请求的客户端
            httpServerResponse.end(Buffer.buffer(serialized));
        }catch(IOException e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
