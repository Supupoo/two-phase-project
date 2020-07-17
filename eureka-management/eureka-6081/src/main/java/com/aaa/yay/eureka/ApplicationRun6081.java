package com.aaa.yay.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author yay
 * @Description TODO
 * @CreatTime 2020年 07月15日 星期三 22:08:45
 */
@SpringBootApplication
@EnableEurekaServer
public class ApplicationRun6081 {
    public static void main(String[] args){
        SpringApplication.run(ApplicationRun6081.class,args);
    }
}
