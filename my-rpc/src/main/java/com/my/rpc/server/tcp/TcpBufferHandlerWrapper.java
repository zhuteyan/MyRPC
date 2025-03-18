package com.my.rpc.server.tcp;

import com.my.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 接收原始的 TCP 数据流（Buffer）。
 * 使用 RecordParser 解析数据流，将字节流拆分为符合协议的消息。
 * 将解析后的完整消息传递给一个用户提供的 Handler<Buffer>
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    /**
     * 构造函数
     * bufferHandler:用户提供的处理器，用于处理解析后的完整消息
     * recordParser：一个 RecordParser 实例，用于解析 TCP 数据流
     * 装饰者模式:（使用 recordParser 对原有的 buffer 处理器能力进行增强）
     */
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }


    //初始化 RecordParser 并设置解析逻辑
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 parser,以固定长度解析数据
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        // 解析逻辑
        parser.setOutput(new Handler<Buffer>() {
            // 消息体长度，初始值为 -1 表示未读取
            int size = -1;
            // 用于存储完整的消息（头 + 体）
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (-1 == size) {
                    // 第一次调用：buffer表示消息头
                    size = buffer.getInt(13);// 从头部的第 13 字节开始读取 4 字节(int)整数，获取消息体的长度
                    parser.fixedSizeMode(size);// 设置解析器的工作模式为固定长度模式
                    resultBuffer.appendBuffer(buffer);//将头部数据保存
                } else {
                    // 第二次调用：buffer表示消息体
                    resultBuffer.appendBuffer(buffer); // 将消息体数据追加到结果
                    bufferHandler.handle(resultBuffer); // 将完整的消息传递给用户提供的处理器
                    // 重置状态，准备解析下一条消息
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }
    @Override
    public void handle(Buffer buffer) {
        //将原始的字节流数据交给 RecordParser 处理
        recordParser.handle(buffer);
    }
}