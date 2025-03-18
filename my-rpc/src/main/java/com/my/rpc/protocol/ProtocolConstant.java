package com.my.rpc.protocol;

/**
 * 协议的常量
 */
public interface ProtocolConstant {

    /**
     * 消息头长度17字节
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 魔数
     * 十六进制以0x或0X开头
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}