package com.aaa.yay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author yay
 * @Description Ftp配置信息
 *      ConfigurationProperties:默认只会从application.prorperties中去读取属性值
 *      所以需要加上@PropertySource来指定配置文件的路径
 * @CreatTime 2020年 07月13日 星期一 21:02:52
 */
@Component
@PropertySource("classpath:properties/ftp.properties")
@ConfigurationProperties(prefix = "spring.ftp")
@Data
public class FtpProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String basePath;
    private String httpPath;

}
