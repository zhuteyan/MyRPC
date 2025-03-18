package com.my.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息的状态
 */
@Getter
public enum ProtocolMessageStatusEnum {

    /**
     * 三个枚举实例,每个实例都有一个对应的文本描述（text）和一个整数值（value）
     * 每个枚举实例都会调用构造函数来初始化自己的属性
     */
    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;

    private final int value;

    /**
     * 构造函数是私有的，这是枚举类的特性，确保枚举实例只能在枚举类内部创建
     * 因此不能在外部通过new关键字创建枚举实例
     */
    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据传入的 value 参数，返回对应的枚举实例
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        //ProtocolMessageStatusEnum.values():获取所有枚举实例
        for (ProtocolMessageStatusEnum anEnum : values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }
}