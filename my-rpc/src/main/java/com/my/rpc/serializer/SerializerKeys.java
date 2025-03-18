package com.my.rpc.serializer;

/**
 * 序列化器键名
 * 这是一个常量接口，通过接口的方式定义了一系列字符串常量
 * 这种定义方式在Java中是常见的，尤其是在需要定义一组相关常量时
 * 接口中的字段默认是 public static final，可以直接通过接口名称访问它们
 */
public interface SerializerKeys {

    String JDK = "jdk";
    String JSON = "json";
    String KRYO = "kryo";
    String HESSIAN = "hessian";

}
