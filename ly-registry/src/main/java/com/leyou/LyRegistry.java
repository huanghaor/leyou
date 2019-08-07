package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 注册中心启动类
 * @author hhr
 * @date 2019-04-28
 */
@EnableEurekaServer
@SpringBootApplication
public class LyRegistry {

    public static void main(String[] args) {
        SpringApplication.run(LyRegistry.class,args);
    }
}
