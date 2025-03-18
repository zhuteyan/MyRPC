package com.my.rpc.protocol;

import com.my.rpc.serializer.Serializer;
import com.my.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {

    /**
     * 编码器：把请求头转换成字节，请求体被序列化成字节
     * protocolMessage：请求头+?类型的请求体(RPCRequest)
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }

        // 向缓冲区buffer写入字节，除了消息体长度
        Buffer buffer = Buffer.buffer();
        ProtocolMessage.Header header = protocolMessage.getHeader();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());// long值转换成字节并追加到buffer末尾

        // 获取序列化器枚举实例
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 依次向缓冲区buffer写入消息体长度和消息体字节数组
        buffer.appendInt(bodyBytes.length);// int值转换成字节并追加到buffer末尾
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}