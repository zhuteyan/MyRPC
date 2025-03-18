package com.my.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.my.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔 - 重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 重试
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                //重试条件：使用 retryIfExceptionOfType()方法指定当出现 Exception 异常时重试
                .retryIfExceptionOfType(Exception.class)
                //重试等待：使用 withWaitStrategy()方法指定策略，选择 fixedWait固定时间间隔策略
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                //重试停止：使用 withStopStrategy() 方法指定策略，选择 stopAfterAttempt 超过最大重试次数停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //重试工作：使用 withRetryListener()监听重试，每次重试除了执行任务外，还打印当前的重试次数
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }

}