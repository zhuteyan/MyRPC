package com.my.rpc.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        //ByteArrayOutputStream实例是一个低级输出流,不会处理数据只会存储数据，用于将字节流转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //ObjectOutputStream实例是一个高级输出流，不会存储数据只会处理数据，用于将对象序列化为字节流
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        //将对象序列化为字节流
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        //将字节流转换为字节数组存入内存
        return outputStream.toByteArray();

        /**
         * 问：把对象序列化为字节流的是ObjectOutputStream，ByteArrayOutputStream是怎么得到字节流的？
         * 答：当将ObjectOutputStream包装在ByteArrayOutputStream外面时，它们形成了一个“管道”。
         * 数据的流动过程如下：ObjectOutputStream将对象序列化为字节流，
         * 这些字节流被写入到它包装的底层流——ByteArrayOutputStream中，
         * 最后ByteArrayOutputStream把流转成字节数组存起来
         *
         * 数据流动过程：
         * 对象 --> [ObjectOutputStream] --> 输出流 --> [ByteArrayOutputStream] --> 字节数组
         */
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            //(T)是一个强制类型转换操作符
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            objectInputStream.close();
        }
        /***
         * 数据流动过程：
         * 字节数组 --> ByteArrayInputStream --> 输入流 --> ObjectInputStream --> 对象
         */
    }
}
