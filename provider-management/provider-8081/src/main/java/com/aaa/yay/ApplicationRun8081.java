package com.aaa.yay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author yay
 * @Description 启动类
 * @CreatTime 2020年 07月13日 星期一 16:46:36
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.aaa.yay.mapper")
public class ApplicationRun8081 {
    public static void main(String[] args){
        SpringApplication.run(ApplicationRun8081.class,args);
    }
}
