package com.my.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    /**
     * 序列化
     * <T>表示这是一个泛型方法
     * byte[]是返回值类型
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     *     <T> 表示这是一个泛型方法
     *     T是返回值类型
     *     Class<T> type：这是目标类型的类对象，用于指定反序列化的目标类型。
     *     例如，如果你想反序列化为 User 类型的对象，type 就是 User.class
     */

    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
