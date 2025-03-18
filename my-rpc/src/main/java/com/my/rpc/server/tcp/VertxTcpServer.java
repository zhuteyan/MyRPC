package com.my.rpc.server.tcp;

import com.my.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        // 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        server.connectHandler(new TcpServerHandler());

        /**
         * 启动 TCP 服务器并监听指定端口
         * server.listen()接收一个Handler<AsyncResult<NetServer>>类型的实例
         * Handler<>的handle方法接收AsyncResult<NetServer>实例result
         * result由 Vert.x 框架自动创建，代表server.listen() 方法操作的结果（成功或失败）
         */
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port " + port);
            } else {
                System.err.println("Failed to start TCP server: " + result.cause());
            }
        });
    }
}