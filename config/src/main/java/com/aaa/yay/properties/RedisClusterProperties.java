package com.aaa.yay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author yay
 * @Description redis配置信息
 *      ConfigurationProperties:默认只会从application.prorperties中去读取属性值
 *      所以需要加上@PropertySource来指定配置文件的路径
 * @CreatTime 2020年 07月13日 星期一 15:58:54
 */
@Component
@PropertySource("classpath:properties/redis_cluster.properties")
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisClusterProperties{
    private String nodes;
    private Integer maxAttempts;
    private Integer commandTimeout;
}