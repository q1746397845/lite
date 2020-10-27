package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunTemplateServerApplication
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/23
 * @Version V1.0
 **/
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class RunTemplateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunTemplateServerApplication.class,args);
    }
}
