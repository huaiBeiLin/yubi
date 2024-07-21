package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.config.RedissonConfig;
import com.yupi.springbootinit.exception.BusinessException;
import org.apache.lucene.store.RateLimiter;
import org.elasticsearch.index.rankeval.RatedRequest;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * packageName com.yupi.springbootinit.manager
 *
 * @author yuxin
 * @version JDK 8
 * @className RedisLimiterManager (此处以class为例)
 * @date 2024/7/16
 * @description redis限流通用模块
 */
@Service
public class RedisLimiterManager {

//    @Resource
//    private RedissonClient redissonClient = redissonConfig.redissonClient();

    public void doRateLimiter(String key) {
        RedissonConfig redissonConfig = new RedissonConfig();
        RRateLimiter rateLimiter = redissonConfig.redissonClient().getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw  new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }

    public static void main(String[] args) {
        RedisLimiterManager redisLimiterManager = new RedisLimiterManager();
        for (int i = 0; i < 5; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("请求1");
            System.out.println("成功第" + i + "次");
            redisLimiterManager.doRateLimiter(stringBuilder.toString());
        }
    }
}
