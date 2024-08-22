package com.yuxin.springbootinit.utils;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;

import java.util.concurrent.ExecutionException;

/**
 * packageName com.yuxin.springbootinit.utils
 * 重试工具类
 */
public class RetryUtil {
    public static <T> void retry(T t, T notValue) {
        Retryer<T> retryer = RetryerBuilder.<T>newBuilder()
                // 非正数进行重试
                .retryIfException()
                // 结果为null报异常
                .retryIfResult(result->result == notValue)
                // 设置最大执行次数3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)).build();

        try {
            retryer.call(()-> t);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (RetryException e) {
            throw new RuntimeException(e);
        }
    }

}
