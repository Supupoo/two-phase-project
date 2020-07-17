package com.aaa.yay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author yay
 * @Description 消费者启动类
 * @CreatTime 2020年 07月15日 星期三 16:41:44
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.aaa.yay"})
public class ApplicationRun7081 {
    public static void main(String[] args){
        SpringApplication.run(ApplicationRun7081.class,args);
    }
}
