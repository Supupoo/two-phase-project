package com.aaa.yay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author yay
 * @Description TODO
 * @CreatTime 2020年 07月17日 星期五 09:29:56
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.aaa.yay.mapper")
public class ApplicationRun8082 {
    public static void main(String[] args){
        SpringApplication.run(ApplicationRun8082.class,args);
    }
}
