package com.my.rpc.fault.retry;

import com.my.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不重试 - 重试策略
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 不重试策略
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        //调用call()方法来执行任务
        return callable.call();
    }

}