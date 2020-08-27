package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName RunEurekaServerApplication
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/27
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaServer
public class RunEurekaServerApplication {
    public static void main(String[] args) {
        System.out.println("ssssssss");
        SpringApplication.run(RunEurekaServerApplication.class,args);
    }
}
