package com.my.rpc.server.tcp;

import com.my.rpc.model.RpcRequest;
import com.my.rpc.model.RpcResponse;
import com.my.rpc.protocol.ProtocolMessage;
import com.my.rpc.protocol.ProtocolMessageDecoder;
import com.my.rpc.protocol.ProtocolMessageEncoder;
import com.my.rpc.protocol.ProtocolMessageTypeEnum;
import com.my.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器: 接受请求buffer数据,解码,得到rpcRequest实例,通过实例和注册中心获取服务实现类和方法
 * 使用方法的返回结果封装rpcResponse，将rpcResponse塞进ProtocolMessage<RpcResponse>的消息体
 * 使用编码器把ProtocolMessage<RpcResponse>编成Buffer数据，发送出去
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        /**
         * 当客户端请求到 Vert.x 的 tcp服务器时，会触发事件，Vert.x框架会负责接收这个请求，
         * 并创建一个NetSocket实例，代表 服务器端和客户端之间的 TCP 连接
         * 将其传递给 TcpServerHandler的 handle方法
         */
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接受请求buffer数据,解码,得到rpcRequest实例
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            // 处理请求，构造rpcResponse实例
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 通过反射获取服务实现类和方法
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                // 获取方法的返回结果
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装rpcResponse
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });

        /**
         * 为 NetSocket设置一个数据处理程序（Handler<Buffer>），以便在 NetSocket 接收到数据时，
         * 能够触发 bufferHandlerWrapper 的处理逻辑
         */
        socket.handler(bufferHandlerWrapper);
    }
}