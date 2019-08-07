package com.lygateway;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 网关中心
 * @author hhr
 * @date 2019-04-28
 */
@EnableZuulProxy
@SpringCloudApplication
public class LyGateWay {
    public static void main(String[] args) {
        SpringApplication.run(LyGateWay.class,args );
    }
}
