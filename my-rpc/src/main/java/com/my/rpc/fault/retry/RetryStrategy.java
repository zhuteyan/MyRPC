package com.my.rpc.fault.retry;

import com.my.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 重试,Callable 是一种用于表示可异步执行的任务的接口
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}