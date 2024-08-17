package com.yuxin.springbootinit.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName com.yuxin.springbootinit.config
 * @author yuxin
 * @version JDK 8
 * @className RedisLimiterConfig (此处以class为例)
 * @date 2024/7/16 * @description 配置Redisson*/

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private Integer database;

    private String host;

    private Integer port;

    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(1)
                .setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
